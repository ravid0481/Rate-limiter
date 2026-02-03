package com.company.ratelimiter.config;

import com.company.ratelimiter.model.RateLimitRule;
import com.company.ratelimiter.strategy.RateLimitStrategyResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Auto-configuration for distributed rate limiter.
 * Initializes all components and registers rate limit rules from configuration.
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "com.company.ratelimiter")
@EnableConfigurationProperties(RateLimiterProperties.class)
@ConditionalOnProperty(
    prefix = "ratelimiter",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class RateLimiterAutoConfiguration {

    private final RateLimiterProperties properties;
    private final RateLimitStrategyResolver strategyResolver;

    public RateLimiterAutoConfiguration(
            RateLimiterProperties properties,
            RateLimitStrategyResolver strategyResolver) {
        this.properties = properties;
        this.strategyResolver = strategyResolver;
    }

    @PostConstruct
    public void init() {
        log.info("=".repeat(80));
        log.info("Distributed Rate Limiter Auto-Configuration");
        log.info("=".repeat(80));
        
        // Register rules from configuration
        List<RateLimitRule> rules = properties.getRules().stream()
            .map(this::convertToRule)
            .collect(Collectors.toList());

        strategyResolver.registerRules(rules);
        
        log.info("Rate Limiter Configuration:");
        log.info("  - Enabled: {}", properties.isEnabled());
        log.info("  - Redis: {}:{}", properties.getRedis().getHost(), properties.getRedis().getPort());
        log.info("  - Circuit Breaker: {} (fallback: {})", 
            properties.getCircuitBreaker().isEnabled(),
            properties.getCircuitBreaker().getFallbackStrategy());
        log.info("  - Rules Registered: {}", rules.size());
        log.info("  - Headers Enabled: {}", properties.getHeaders().isIncludeRateLimitHeaders());
        log.info("=".repeat(80));
    }

    private RateLimitRule convertToRule(RateLimiterProperties.RuleConfig config) {
        return RateLimitRule.builder()
            .dimension(config.getDimension())
            .limit(config.getLimit())
            .window(config.getWindow())
            .priority(config.getPriority())
            .enabled(config.isEnabled())
            .identifier(config.getIdentifier())
            .description(config.getDescription())
            .build();
    }
}
