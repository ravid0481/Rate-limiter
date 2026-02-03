package com.company.ratelimiter.fallback;

import com.company.ratelimiter.core.RateLimitContext;
import com.company.ratelimiter.core.RateLimitDecision;
import com.company.ratelimiter.model.RateLimitDimension;
import com.company.ratelimiter.model.RateLimitKey;
import com.company.ratelimiter.model.RateLimitRule;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Balanced fallback strategy - uses local in-memory rate limiting when Redis is unavailable.
 * 
 * Trade-offs:
 * - Eventual consistency: Each instance has its own counter (not distributed)
 * - Better than nothing: Provides some rate limiting protection
 * - Automatic cleanup: Entries expire after window duration
 * 
 * Use when: You want best-effort rate limiting during Redis outages
 */
@Slf4j
@Component
@ConditionalOnProperty(
    prefix = "ratelimiter.circuit-breaker",
    name = "fallback-strategy",
    havingValue = "LOCAL_CACHE",
    matchIfMissing = true  // Default fallback strategy
)
public class LocalCacheFallback implements FallbackStrategy {

    // Cache structure: Key -> {timestamps of requests in window}
    private final Cache<String, RequestWindow> localCache;

    public LocalCacheFallback() {
        this.localCache = Caffeine.newBuilder()
            .maximumSize(10_000)  // Limit memory usage
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats()
            .build();
        
        log.info("LocalCacheFallback initialized with max size: 10,000 entries");
    }

    @Override
    public RateLimitDecision onExecutorUnavailable(List<RateLimitRule> rules, RateLimitContext context) {
        log.warn("Redis unavailable - using LOCAL_CACHE fallback for request: {}", 
            context.getRequestId());

        // Check each rule
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

            RateLimitDecision decision = checkLocalLimit(key, rule, context);
            
            if (!decision.isAllowed()) {
                decision.setFromFallback(true);
                decision.setContext("FALLBACK:LOCAL_CACHE:" + key);
                return decision;
            }
        }

        // All limits passed
        RateLimitDecision decision = RateLimitDecision.allowedFallback();
        decision.setContext("FALLBACK:LOCAL_CACHE");
        return decision;
    }

    /**
     * Check rate limit using local cache (per-instance)
     * Uses sliding window log algorithm similar to Redis
     */
    private RateLimitDecision checkLocalLimit(
            RateLimitKey key, 
            RateLimitRule rule,
            RateLimitContext context) {
        
        String cacheKey = key.toRedisKey();
        long currentMillis = System.currentTimeMillis();
        long windowStartMillis = currentMillis - rule.getWindowMillis();

        RequestWindow window = localCache.get(cacheKey, k -> new RequestWindow());

        synchronized (window) {
            // Remove expired timestamps
            window.removeExpiredRequests(windowStartMillis);

            // Check if limit exceeded
            long currentCount = window.getRequestCount();

            if (currentCount >= rule.getLimit()) {
                // DENIED
                long resetTime = window.getOldestTimestamp() + rule.getWindowMillis();
                return RateLimitDecision.denied(
                    rule.getLimit(),
                    resetTime / 1000,
                    rule.getDimension(),
                    cacheKey
                );
            }

            // ALLOWED - add new request
            window.addRequest(currentMillis);

            long remaining = rule.getLimit() - currentCount - 1;
            long resetTime = (currentMillis + rule.getWindowMillis()) / 1000;

            return RateLimitDecision.allowed(rule.getLimit(), remaining, resetTime);
        }
    }

    @Override
    public String getStrategyName() {
        return "LOCAL_CACHE";
    }

    /**
     * Get cache statistics for monitoring
     */
    public String getCacheStats() {
        return localCache.stats().toString();
    }

    /**
     * Clear local cache (useful for testing)
     */
    public void clearCache() {
        localCache.invalidateAll();
        log.info("Local cache cleared");
    }

    /**
     * Internal class to track requests in a sliding window
     */
    private static class RequestWindow {
        // Using ConcurrentHashMap to store timestamps
        // Key: timestamp, Value: count (usually 1, but supports duplicate timestamps)
        private final ConcurrentHashMap<Long, AtomicLong> timestamps = new ConcurrentHashMap<>();

        void addRequest(long timestamp) {
            timestamps.computeIfAbsent(timestamp, k -> new AtomicLong(0)).incrementAndGet();
        }

        void removeExpiredRequests(long windowStart) {
            timestamps.entrySet().removeIf(entry -> entry.getKey() < windowStart);
        }

        long getRequestCount() {
            return timestamps.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
        }

        long getOldestTimestamp() {
            return timestamps.keySet().stream()
                .min(Long::compare)
                .orElse(System.currentTimeMillis());
        }
    }
}
