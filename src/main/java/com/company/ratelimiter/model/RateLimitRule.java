package com.company.ratelimiter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

/**
 * Represents a rate limiting rule configuration.
 * Defines how many requests are allowed within a specific time window for a given dimension.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitRule {
    
    /**
     * The dimension this rule applies to (USER, IP, API_KEY, etc.)
     */
    private RateLimitDimension dimension;
    
    /**
     * Maximum number of requests allowed within the window
     */
    private long limit;
    
    /**
     * Time window duration (e.g., 60 seconds, 1 minute)
     */
    private Duration window;
    
    /**
     * Priority of this rule (lower number = higher priority)
     * Used when multiple rules apply to the same request
     */
    @Builder.Default
    private int priority = 100;
    
    /**
     * Whether this rule is currently enabled
     */
    @Builder.Default
    private boolean enabled = true;
    
    /**
     * Optional: specific identifier this rule applies to
     * If null, applies to all identifiers of this dimension
     * Example: userId="12345" for a specific user override
     */
    private String identifier;
    
    /**
     * Optional: description for documentation/debugging
     */
    private String description;

    /**
     * Get window in seconds
     */
    public long getWindowSeconds() {
        return window.getSeconds();
    }

    /**
     * Get window in milliseconds
     */
    public long getWindowMillis() {
        return window.toMillis();
    }

    /**
     * Calculate TTL with buffer for Redis key expiration
     */
    public long getTtlSeconds() {
        // Add 20 second buffer to handle clock drift and in-flight requests
        return getWindowSeconds() + 20;
    }

    /**
     * Check if this rule applies to a specific identifier
     */
    public boolean appliesTo(String identifier) {
        return this.identifier == null || this.identifier.equals(identifier);
    }
}
