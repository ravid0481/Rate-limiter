package com.company.ratelimiter.executor;

import com.company.ratelimiter.core.RateLimitContext;
import com.company.ratelimiter.core.RateLimitDecision;
import com.company.ratelimiter.exception.RedisUnavailableException;
import com.company.ratelimiter.model.RateLimitKey;
import com.company.ratelimiter.model.RateLimitRule;
import com.company.ratelimiter.scripts.LuaScriptLoader;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Redis-based rate limit executor using Sliding Window Log algorithm.
 * Executes Lua scripts atomically to ensure consistency across distributed instances.
 */
@Slf4j
@Component
public class RedisRateLimitExecutor implements RateLimitExecutor {

    private final RedisTemplate<String, String> redisTemplate;
    private final LuaScriptLoader luaScriptLoader;
    private final MeterRegistry meterRegistry;

    // Cached Lua scripts
    private final DefaultRedisScript<List> singleLimitScript;
    private final DefaultRedisScript<List> multiLimitScript;

    public RedisRateLimitExecutor(
            RedisTemplate<String, String> redisTemplate,
            LuaScriptLoader luaScriptLoader,
            MeterRegistry meterRegistry) {
        
        this.redisTemplate = redisTemplate;
        this.luaScriptLoader = luaScriptLoader;
        this.meterRegistry = meterRegistry;

        // Initialize Lua scripts
        this.singleLimitScript = new DefaultRedisScript<>();
        this.singleLimitScript.setScriptText(luaScriptLoader.getSlidingWindowLogScript());
        this.singleLimitScript.setResultType(List.class);

        this.multiLimitScript = new DefaultRedisScript<>();
        this.multiLimitScript.setScriptText(luaScriptLoader.getSlidingWindowMultiScript());
        this.multiLimitScript.setResultType(List.class);
    }

    @Override
    public RateLimitDecision checkLimit(RateLimitRule rule, RateLimitContext context) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            String identifier = context.getIdentifier(rule.getDimension());
            if (identifier == null || identifier.isEmpty()) {
                log.warn("No identifier found for dimension: {}", rule.getDimension());
                return RateLimitDecision.allowed(rule.getLimit(), rule.getLimit(), 
                    System.currentTimeMillis() / 1000 + rule.getWindowSeconds());
            }

            RateLimitKey key = new RateLimitKey(
                rule.getDimension(),
                identifier,
                rule.getWindowSeconds()
            );

            List<Object> result = executeSingleLimitScript(key, rule, context);
            RateLimitDecision decision = parseScriptResult(result, rule, key);

            recordMetrics(decision, rule, sample);
            return decision;

        } catch (Exception e) {
            log.error("Redis error during rate limit check", e);
            recordMetrics(null, rule, sample);
            throw new RedisUnavailableException("Failed to check rate limit in Redis", e);
        }
    }

    @Override
    public RateLimitDecision checkLimits(List<RateLimitRule> rules, RateLimitContext context) {
        if (rules.isEmpty()) {
            return RateLimitDecision.allowed(Long.MAX_VALUE, Long.MAX_VALUE, 
                System.currentTimeMillis() / 1000 + 60);
        }

        if (rules.size() == 1) {
            return checkLimit(rules.get(0), context);
        }

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            List<Object> result = executeMultiLimitScript(rules, context);
            RateLimitDecision decision = parseMultiScriptResult(result, rules, context);

            recordMetrics(decision, rules.get(0), sample);
            return decision;

        } catch (Exception e) {
            log.error("Redis error during multi-dimension rate limit check", e);
            recordMetrics(null, rules.get(0), sample);
            throw new RedisUnavailableException("Failed to check rate limits in Redis", e);
        }
    }

    /**
     * Execute single limit Lua script
     */
    private List<Object> executeSingleLimitScript(
            RateLimitKey key, 
            RateLimitRule rule, 
            RateLimitContext context) {
        
        List<String> keys = Collections.singletonList(key.toRedisKey());
        List<String> args = List.of(
            String.valueOf(rule.getLimit()),
            String.valueOf(rule.getWindowMillis()),
            context.getRequestId(),
            String.valueOf(rule.getTtlSeconds())
        );

        return redisTemplate.execute(singleLimitScript, keys, args.toArray());
    }

    /**
     * Execute multi-dimension Lua script
     * This ensures atomicity - either ALL limits pass and ALL counters increment,
     * or ANY limit fails and NO counters increment.
     */
    private List<Object> executeMultiLimitScript(
            List<RateLimitRule> rules, 
            RateLimitContext context) {
        
        List<String> keys = new ArrayList<>();
        List<String> args = new ArrayList<>();

        // Add number of dimensions
        args.add(String.valueOf(rules.size()));

        // Build keys and args for each rule
        for (RateLimitRule rule : rules) {
            String identifier = context.getIdentifier(rule.getDimension());
            if (identifier == null || identifier.isEmpty()) {
                continue;
            }

            RateLimitKey key = new RateLimitKey(
                rule.getDimension(),
                identifier,
                rule.getWindowSeconds()
            );

            keys.add(key.toRedisKey());

            // Add triplet: limit, window_millis, ttl_seconds
            args.add(String.valueOf(rule.getLimit()));
            args.add(String.valueOf(rule.getWindowMillis()));
            args.add(String.valueOf(rule.getTtlSeconds()));
        }

        // Add request ID at the end
        args.add(context.getRequestId());

        return redisTemplate.execute(multiLimitScript, keys, args.toArray());
    }

    /**
     * Parse result from single limit script
     * Returns: {allowed, remaining, reset_time}
     */
    private RateLimitDecision parseScriptResult(
            List<Object> result, 
            RateLimitRule rule,
            RateLimitKey key) {
        
        if (result == null || result.size() < 3) {
            throw new IllegalStateException("Invalid script result");
        }

        long allowed = ((Number) result.get(0)).longValue();
        long remaining = ((Number) result.get(1)).longValue();
        long resetTime = ((Number) result.get(2)).longValue();

        if (allowed == 1) {
            return RateLimitDecision.allowed(rule.getLimit(), remaining, resetTime);
        } else {
            return RateLimitDecision.denied(
                rule.getLimit(), 
                resetTime, 
                rule.getDimension(),
                key.toString()
            );
        }
    }

    /**
     * Parse result from multi-dimension script
     * Returns: {allowed, failed_dimension_index, remaining, reset_time}
     */
    private RateLimitDecision parseMultiScriptResult(
            List<Object> result, 
            List<RateLimitRule> rules,
            RateLimitContext context) {
        
        if (result == null || result.size() < 4) {
            throw new IllegalStateException("Invalid multi-dimension script result");
        }

        long allowed = ((Number) result.get(0)).longValue();
        long failedIndex = ((Number) result.get(1)).longValue();
        long remaining = ((Number) result.get(2)).longValue();
        long resetTime = ((Number) result.get(3)).longValue();

        if (allowed == 1) {
            // All limits passed
            return RateLimitDecision.allowed(
                rules.stream().mapToLong(RateLimitRule::getLimit).min().orElse(0),
                remaining,
                resetTime
            );
        } else {
            // One limit failed
            RateLimitRule failedRule = rules.get((int) failedIndex - 1);
            String identifier = context.getIdentifier(failedRule.getDimension());
            
            return RateLimitDecision.denied(
                failedRule.getLimit(),
                resetTime,
                failedRule.getDimension(),
                failedRule.getDimension() + ":" + identifier
            );
        }
    }

    /**
     * Record metrics for monitoring
     */
    private void recordMetrics(RateLimitDecision decision, RateLimitRule rule, Timer.Sample sample) {
        sample.stop(Timer.builder("ratelimiter.check.latency")
            .tag("dimension", rule.getDimension().getValue())
            .tag("allowed", decision != null ? String.valueOf(decision.isAllowed()) : "error")
            .register(meterRegistry));

        if (decision != null) {
            meterRegistry.counter("ratelimiter.requests",
                "dimension", rule.getDimension().getValue(),
                "result", decision.isAllowed() ? "allowed" : "denied"
            ).increment();
        } else {
            meterRegistry.counter("ratelimiter.requests",
                "dimension", rule.getDimension().getValue(),
                "result", "error"
            ).increment();
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            redisTemplate.execute(connection -> {
                connection.ping();
                return true;
            });
            return true;
        } catch (Exception e) {
            log.warn("Redis availability check failed", e);
            return false;
        }
    }

    @Override
    public String getExecutorType() {
        return "REDIS";
    }
}
