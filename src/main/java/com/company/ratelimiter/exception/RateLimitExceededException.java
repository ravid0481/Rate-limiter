package com.company.ratelimiter.exception;

import com.company.ratelimiter.core.RateLimitDecision;
import lombok.Getter;

/**
 * Exception thrown when rate limit is exceeded.
 * Used to trigger 429 Too Many Requests response.
 */
@Getter
public class RateLimitExceededException extends RateLimiterException {

    private final RateLimitDecision decision;

    public RateLimitExceededException(RateLimitDecision decision) {
        super(String.format(
            "Rate limit exceeded for %s. Limit: %d, Reset in: %d seconds",
            decision.getContext(),
            decision.getLimit(),
            decision.getRetryAfterSeconds()
        ));
        this.decision = decision;
    }

    public long getRetryAfterSeconds() {
        return decision.getRetryAfterSeconds();
    }

    public long getLimit() {
        return decision.getLimit();
    }

    public long getResetTime() {
        return decision.getResetTime();
    }
}
