package com.company.ratelimiter.exception;

/**
 * Exception thrown when Redis is unavailable or experiencing errors
 */
public class RedisUnavailableException extends RateLimiterException {

    public RedisUnavailableException(String message) {
        super(message);
    }

    public RedisUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
