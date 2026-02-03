package com.company.ratelimiter.executor;

import com.company.ratelimiter.core.RateLimitContext;
import com.company.ratelimiter.core.RateLimitDecision;
import com.company.ratelimiter.model.RateLimitRule;

import java.util.List;

/**
 * Interface for executing rate limit checks against a storage backend.
 * Allows multiple implementations (Redis, local cache, etc.)
 */
public interface RateLimitExecutor {

    /**
     * Check a single rate limit rule
     * 
     * @param rule The rate limit rule to check
     * @param context Request context
     * @return Decision whether to allow or deny the request
     */
    RateLimitDecision checkLimit(RateLimitRule rule, RateLimitContext context);

    /**
     * Check multiple rate limit rules atomically.
     * All rules must pass for the request to be allowed.
     * If any rule fails, NO counters are incremented.
     * 
     * @param rules List of rules to check
     * @param context Request context
     * @return Decision based on all rules
     */
    RateLimitDecision checkLimits(List<RateLimitRule> rules, RateLimitContext context);

    /**
     * Check if this executor is currently available
     */
    boolean isAvailable();

    /**
     * Get executor type name for logging/metrics
     */
    String getExecutorType();
}
