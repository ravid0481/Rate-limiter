package com.company.ratelimiter.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Circuit breaker configuration for rate limiter.
 * Protects against Redis failures and triggers fallback strategies.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(
    prefix = "ratelimiter.circuit-breaker",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class CircuitBreakerConfiguration {

    private final RateLimiterProperties properties;

    public CircuitBreakerConfiguration(RateLimiterProperties properties) {
        this.properties = properties;
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        RateLimiterProperties.CircuitBreakerConfig cbConfig = properties.getCircuitBreaker();

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            // Percentage of failed calls before opening circuit
            .failureRateThreshold(cbConfig.getFailureRateThreshold())
            
            // Minimum number of calls before calculating failure rate
            .minimumNumberOfCalls(10)
            
            // Time to wait in OPEN state before transitioning to HALF_OPEN
            .waitDurationInOpenState(cbConfig.getWaitDurationInOpenState())
            
            // Number of test calls in HALF_OPEN state
            .permittedNumberOfCallsInHalfOpenState(cbConfig.getPermittedCallsInHalfOpen())
            
            // Sliding window size for recording outcomes
            .slidingWindowSize(100)
            
            // Use count-based sliding window
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            
            // Automatically transition from OPEN to HALF_OPEN
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            
            // Record these exceptions as failures
            .recordExceptions(Exception.class)
            
            .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        
        log.info("Circuit Breaker configured with failure rate threshold: {}%, wait duration: {} seconds",
            cbConfig.getFailureRateThreshold(),
            cbConfig.getWaitDurationInOpenState().getSeconds());
        
        return registry;
    }
}
