package com.company.ratelimiter.strategy;

import com.company.ratelimiter.core.RateLimitContext;
import com.company.ratelimiter.model.RateLimitRule;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Resolves which rate limit rules apply to a given request.
 * Supports priority-based rule selection and caching for performance.
 */
@Slf4j
@Component
public class RateLimitStrategyResolver {

    private final List<RateLimitRule> globalRules = new ArrayList<>();
    
    // Cache resolved rules per dimension combination
    private final Cache<String, List<RateLimitRule>> ruleCache;

    public RateLimitStrategyResolver() {
        this.ruleCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(60, TimeUnit.SECONDS)  // Cache for 60 seconds
            .build();
    }

    /**
     * Register global rate limit rules
     */
    public void registerRules(List<RateLimitRule> rules) {
        synchronized (globalRules) {
            globalRules.clear();
            globalRules.addAll(rules);
            
            // Sort by priority (lower number = higher priority)
            globalRules.sort(Comparator.comparingInt(RateLimitRule::getPriority));
            
            log.info("Registered {} rate limit rules", rules.size());
            rules.forEach(rule -> 
                log.info("  - {}: {} req/{} sec (priority: {})", 
                    rule.getDimension(), 
                    rule.getLimit(), 
                    rule.getWindowSeconds(),
                    rule.getPriority())
            );
        }
        
        // Clear cache when rules change
        ruleCache.invalidateAll();
    }

    /**
     * Add a single rule (useful for dynamic configuration)
     */
    public void addRule(RateLimitRule rule) {
        synchronized (globalRules) {
            globalRules.add(rule);
            globalRules.sort(Comparator.comparingInt(RateLimitRule::getPriority));
        }
        ruleCache.invalidateAll();
        log.info("Added rate limit rule: {}", rule.getDimension());
    }

    /**
     * Resolve applicable rules for a given context
     * Returns rules sorted by priority (most restrictive first)
     */
    public List<RateLimitRule> resolveRules(RateLimitContext context) {
        String cacheKey = buildCacheKey(context);
        
        return ruleCache.get(cacheKey, k -> {
            List<RateLimitRule> applicableRules = new ArrayList<>();
            
            synchronized (globalRules) {
                for (RateLimitRule rule : globalRules) {
                    if (!rule.isEnabled()) {
                        continue;
                    }

                    // Check if context has identifier for this dimension
                    if (!context.hasIdentifier(rule.getDimension())) {
                        continue;
                    }

                    String identifier = context.getIdentifier(rule.getDimension());
                    
                    // Check if rule applies to this specific identifier
                    if (rule.appliesTo(identifier)) {
                        applicableRules.add(rule);
                    }
                }
            }

            log.debug("Resolved {} applicable rules for context: {}", 
                applicableRules.size(), context.getRequestId());
            
            return applicableRules;
        });
    }

    /**
     * Build cache key from context dimensions
     */
    private String buildCacheKey(RateLimitContext context) {
        StringBuilder key = new StringBuilder();
        
        if (context.getUserId() != null) {
            key.append("U:").append(context.getUserId()).append("|");
        }
        if (context.getIpAddress() != null) {
            key.append("IP:").append(context.getIpAddress()).append("|");
        }
        if (context.getApiKey() != null) {
            key.append("API:").append(context.getApiKey()).append("|");
        }
        if (context.getTenantId() != null) {
            key.append("T:").append(context.getTenantId()).append("|");
        }
        if (context.getRequestUri() != null) {
            key.append("EP:").append(context.getRequestUri());
        }
        
        return key.length() > 0 ? key.toString() : "EMPTY";
    }

    /**
     * Get all registered rules (for debugging/monitoring)
     */
    public List<RateLimitRule> getAllRules() {
        synchronized (globalRules) {
            return new ArrayList<>(globalRules);
        }
    }

    /**
     * Clear rule cache (useful for testing)
     */
    public void clearCache() {
        ruleCache.invalidateAll();
    }
}
