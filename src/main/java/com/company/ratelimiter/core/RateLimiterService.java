package com.company.ratelimiter.core;

import com.company.ratelimiter.exception.RateLimitExceededException;
import com.company.ratelimiter.exception.RedisUnavailableException;
import com.company.ratelimiter.executor.RateLimitExecutor;
import com.company.ratelimiter.fallback.FallbackStrategy;
import com.company.ratelimiter.model.RateLimitRule;
import com.company.ratelimiter.strategy.RateLimitStrategyResolver;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

/**
 * Main rate limiter service that orchestrates rate limit checks.
 * Integrates with Redis executor, circuit breaker, and fallback strategies.
 */
@Slf4j
@Service
public class RateLimiterService {

    private final RateLimitExecutor redisExecutor;
    private final RateLimitStrategyResolver strategyResolver;
    private final FallbackStrategy fallbackStrategy;
    private final CircuitBreaker circuitBreaker;
    private final MeterRegistry meterRegistry;

    public RateLimiterService(
            @Qualifier("redisRateLimitExecutor") RateLimitExecutor redisExecutor,
            RateLimitStrategyResolver strategyResolver,
            FallbackStrategy fallbackStrategy,
            CircuitBreakerRegistry circuitBreakerRegistry,
            MeterRegistry meterRegistry) {
        
        this.redisExecutor = redisExecutor;
        this.strategyResolver = strategyResolver;
        this.fallbackStrategy = fallbackStrategy;
        this.meterRegistry = meterRegistry;
        
        // Get or create circuit breaker for rate limiter
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("rateLimiterCircuitBreaker");
        
        // Log circuit breaker state transitions
        this.circuitBreaker.getEventPublisher()
            .onStateTransition(event -> {
                log.warn("Circuit Breaker state transition: {} -> {}", 
                    event.getStateTransition().getFromState(),
                    event.getStateTransition().getToState());
                
                meterRegistry.counter("ratelimiter.circuitbreaker.state.transition",
                    "from", event.getStateTransition().getFromState().name(),
                    "to", event.getStateTransition().getToState().name()
                ).increment();
            });
        
        this.circuitBreaker.getEventPublisher()
            .onSuccess(event -> log.debug("Circuit Breaker success"))
            .onError(event -> log.warn("Circuit Breaker error: {}", event.getThrowable().getMessage()))
            .onCallNotPermitted(event -> log.warn("Circuit Breaker OPEN - call not permitted"));

        log.info("RateLimiterService initialized with fallback strategy: {}", 
            fallbackStrategy.getStrategyName());
    }

    /**
     * Check if a request should be allowed based on rate limits.
     * Throws RateLimitExceededException if denied.
     * 
     * @param context Request context
     * @throws RateLimitExceededException if rate limit is exceeded
     */
    public void checkRateLimit(RateLimitContext context) {
        RateLimitDecision decision = evaluateRateLimit(context);
        
        if (!decision.isAllowed()) {
            meterRegistry.counter("ratelimiter.requests.denied",
                "dimension", decision.getDeniedBy() != null ? decision.getDeniedBy().getValue() : "unknown",
                "fallback", String.valueOf(decision.isFromFallback())
            ).increment();
            
            throw new RateLimitExceededException(decision);
        }
        
        meterRegistry.counter("ratelimiter.requests.allowed",
            "fallback", String.valueOf(decision.isFromFallback())
        ).increment();
    }

    /**
     * Evaluate rate limit and return decision without throwing exception.
     * 
     * @param context Request context
     * @return Rate limit decision
     */
    public RateLimitDecision evaluateRateLimit(RateLimitContext context) {
        // Resolve applicable rules for this context
        List<RateLimitRule> applicableRules = strategyResolver.resolveRules(context);
        
        if (applicableRules.isEmpty()) {
            log.debug("No applicable rate limit rules for request: {}", context.getRequestId());
            return RateLimitDecision.allowed(Long.MAX_VALUE, Long.MAX_VALUE, 
                System.currentTimeMillis() / 1000 + 60);
        }

        log.debug("Checking {} rate limit rules for request: {}", 
            applicableRules.size(), context.getRequestId());

        // Execute rate limit check with circuit breaker protection
        return executeWithCircuitBreaker(applicableRules, context);
    }

    /**
     * Execute rate limit check with circuit breaker.
     * Falls back to FallbackStrategy if Redis is unavailable.
     */
    private RateLimitDecision executeWithCircuitBreaker(
            List<RateLimitRule> rules, 
            RateLimitContext context) {
        
        Supplier<RateLimitDecision> rateLimitCheck = () -> {
            try {
                // Use multi-dimension check for atomicity
                return redisExecutor.checkLimits(rules, context);
            } catch (RedisUnavailableException e) {
                log.error("Redis unavailable during rate limit check", e);
                throw e;  // Let circuit breaker handle
            }
        };

        try {
            // Execute with circuit breaker protection
            return circuitBreaker.executeSupplier(rateLimitCheck);
            
        } catch (Exception e) {
            // Circuit breaker is OPEN or Redis failed
            log.warn("Falling back to {} strategy due to: {}", 
                fallbackStrategy.getStrategyName(), e.getMessage());
            
            meterRegistry.counter("ratelimiter.fallback.triggered",
                "strategy", fallbackStrategy.getStrategyName()
            ).increment();
            
            return fallbackStrategy.onExecutorUnavailable(rules, context);
        }
    }

    /**
     * Check if rate limiter is healthy (Redis is available)
     */
    public boolean isHealthy() {
        return redisExecutor.isAvailable() && 
               circuitBreaker.getState() != CircuitBreaker.State.OPEN;
    }

    /**
     * Get circuit breaker state for monitoring
     */
    public String getCircuitBreakerState() {
        return circuitBreaker.getState().name();
    }

    /**
     * Get circuit breaker metrics
     */
    public CircuitBreaker.Metrics getCircuitBreakerMetrics() {
        return circuitBreaker.getMetrics();
    }
}
