package com.company.ratelimiter.fallback;

import com.company.ratelimiter.core.RateLimitContext;
import com.company.ratelimiter.core.RateLimitDecision;
import com.company.ratelimiter.model.RateLimitRule;

import java.util.List;

/**
 * Interface for fallback strategies when primary executor (Redis) is unavailable.
 * Implementations provide different behaviors during Redis outages.
 */
public interface FallbackStrategy {

    /**
     * Make a rate limit decision when primary executor is unavailable
     * 
     * @param rules Rate limit rules that would have been checked
     * @param context Request context
     * @return Fallback decision
     */
    RateLimitDecision onExecutorUnavailable(List<RateLimitRule> rules, RateLimitContext context);

    /**
     * Get strategy name for logging/metrics
     */
    String getStrategyName();
}
