package com.company.ratelimiter.exception;

/**
 * Base exception for all rate limiter related errors
 */
public class RateLimiterException extends RuntimeException {

    public RateLimiterException(String message) {
        super(message);
    }

    public RateLimiterException(String message, Throwable cause) {
        super(message, cause);
    }
}
