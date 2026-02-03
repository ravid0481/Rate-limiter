package com.company.ratelimiter.fallback;

import com.company.ratelimiter.core.RateLimitContext;
import com.company.ratelimiter.core.RateLimitDecision;
import com.company.ratelimiter.model.RateLimitRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Optimistic fallback strategy - allows all requests when Redis is unavailable.
 * 
 * Use when: Service availability is more important than strict rate limiting
 * Risk: No rate limiting during Redis outages (potential system overload)
 */
@Slf4j
@Component
@ConditionalOnProperty(
    prefix = "ratelimiter.circuit-breaker",
    name = "fallback-strategy",
    havingValue = "ALLOW_ALL"
)
public class AllowAllFallback implements FallbackStrategy {

    @Override
    public RateLimitDecision onExecutorUnavailable(List<RateLimitRule> rules, RateLimitContext context) {
        log.warn("Redis unavailable - ALLOWING request (fallback: ALLOW_ALL) for context: {}", 
            context.getRequestId());
        
        RateLimitDecision decision = RateLimitDecision.allowedFallback();
        decision.setContext("FALLBACK:ALLOW_ALL");
        return decision;
    }

    @Override
    public String getStrategyName() {
        return "ALLOW_ALL";
    }
}
