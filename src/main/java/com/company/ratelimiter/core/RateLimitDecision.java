package com.company.ratelimiter.core;

import com.company.ratelimiter.model.RateLimitDimension;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Result of a rate limit check.
 * Contains the decision (allowed/denied) and metadata for HTTP headers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitDecision {
    
    /**
     * Whether the request is allowed
     */
    private boolean allowed;
    
    /**
     * Maximum number of requests allowed in the window
     */
    private long limit;
    
    /**
     * Number of requests remaining in current window
     */
    private long remaining;
    
    /**
     * Timestamp when the rate limit window resets (Unix epoch seconds)
     */
    private long resetTime;
    
    /**
     * The dimension that caused the denial (if denied)
     */
    private RateLimitDimension deniedBy;
    
    /**
     * Additional context (e.g., "USER:12345", "IP:192.168.1.1")
     */
    private String context;
    
    /**
     * Whether this decision came from fallback (not Redis)
     */
    @Builder.Default
    private boolean fromFallback = false;

    /**
     * Create an ALLOWED decision
     */
    public static RateLimitDecision allowed(long limit, long remaining, long resetTime) {
        return RateLimitDecision.builder()
                .allowed(true)
                .limit(limit)
                .remaining(remaining)
                .resetTime(resetTime)
                .build();
    }

    /**
     * Create a DENIED decision
     */
    public static RateLimitDecision denied(long limit, long resetTime, RateLimitDimension deniedBy, String context) {
        return RateLimitDecision.builder()
                .allowed(false)
                .limit(limit)
                .remaining(0)
                .resetTime(resetTime)
                .deniedBy(deniedBy)
                .context(context)
                .build();
    }

    /**
     * Create a fallback ALLOWED decision (when Redis is down)
     */
    public static RateLimitDecision allowedFallback() {
        return RateLimitDecision.builder()
                .allowed(true)
                .limit(-1)
                .remaining(-1)
                .resetTime(Instant.now().getEpochSecond() + 60)
                .fromFallback(true)
                .build();
    }

    /**
     * Create a fallback DENIED decision (when Redis is down)
     */
    public static RateLimitDecision deniedFallback() {
        return RateLimitDecision.builder()
                .allowed(false)
                .limit(-1)
                .remaining(0)
                .resetTime(Instant.now().getEpochSecond() + 60)
                .fromFallback(true)
                .build();
    }

    /**
     * Get retry-after value in seconds (for 429 responses)
     */
    public long getRetryAfterSeconds() {
        long now = Instant.now().getEpochSecond();
        return Math.max(0, resetTime - now);
    }
}
