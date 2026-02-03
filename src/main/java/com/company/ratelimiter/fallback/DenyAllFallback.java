package com.company.ratelimiter.fallback;

import com.company.ratelimiter.core.RateLimitContext;
import com.company.ratelimiter.core.RateLimitDecision;
import com.company.ratelimiter.model.RateLimitRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Pessimistic fallback strategy - denies all requests when Redis is unavailable.
 * 
 * Use when: Data protection is more important than availability
 * Risk: Service downtime during Redis outages
 */
@Slf4j
@Component
@ConditionalOnProperty(
    prefix = "ratelimiter.circuit-breaker",
    name = "fallback-strategy",
    havingValue = "DENY_ALL"
)
public class DenyAllFallback implements FallbackStrategy {

    @Override
    public RateLimitDecision onExecutorUnavailable(List<RateLimitRule> rules, RateLimitContext context) {
        log.warn("Redis unavailable - DENYING request (fallback: DENY_ALL) for context: {}", 
            context.getRequestId());
        
        RateLimitDecision decision = RateLimitDecision.deniedFallback();
        decision.setContext("FALLBACK:DENY_ALL");
        return decision;
    }

    @Override
    public String getStrategyName() {
        return "DENY_ALL";
    }
}
