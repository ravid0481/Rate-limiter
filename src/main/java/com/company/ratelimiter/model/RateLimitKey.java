package com.company.ratelimiter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Composite key for rate limiting.
 * Used to uniquely identify a rate limit scope in Redis.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitKey {
    
    /**
     * The dimension (USER, IP, API_KEY, etc.)
     */
    private RateLimitDimension dimension;
    
    /**
     * The specific identifier value (userId, IP address, API key value, etc.)
     */
    private String identifier;
    
    /**
     * Window size in seconds
     */
    private long windowSeconds;

    /**
     * Build Redis key in format: ratelimit:{dimension}:{identifier}:{window}
     * 
     * Examples:
     * - ratelimit:user:12345:60
     * - ratelimit:ip:192.168.1.1:300
     * - ratelimit:apikey:abc123xyz:3600
     */
    public String toRedisKey() {
        return String.format("ratelimit:%s:%s:%d", 
            dimension.getValue(), 
            identifier, 
            windowSeconds);
    }

    /**
     * Build config key in format: ratelimit:config:{dimension}:{identifier}
     */
    public String toConfigKey() {
        return String.format("ratelimit:config:%s:%s", 
            dimension.getValue(), 
            identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RateLimitKey that = (RateLimitKey) o;
        return windowSeconds == that.windowSeconds &&
               dimension == that.dimension &&
               Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimension, identifier, windowSeconds);
    }

    @Override
    public String toString() {
        return toRedisKey();
    }
}
