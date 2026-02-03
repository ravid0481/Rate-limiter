package com.company.ratelimiter.metrics;

import com.company.ratelimiter.core.RateLimiterService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * Health indicator for rate limiter.
 * Integrated with Spring Boot Actuator for monitoring.
 */
@Slf4j
@Component
@ConditionalOnClass(HealthIndicator.class)
public class RateLimiterHealthIndicator implements HealthIndicator {

    private final RateLimiterService rateLimiterService;

    public RateLimiterHealthIndicator(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public Health health() {
        try {
            boolean isHealthy = rateLimiterService.isHealthy();
            String circuitBreakerState = rateLimiterService.getCircuitBreakerState();
            CircuitBreaker.Metrics metrics = rateLimiterService.getCircuitBreakerMetrics();

            if (isHealthy && "CLOSED".equals(circuitBreakerState)) {
                return Health.up()
                    .withDetail("circuitBreakerState", circuitBreakerState)
                    .withDetail("failureRate", String.format("%.2f%%", metrics.getFailureRate()))
                    .withDetail("numberOfCalls", metrics.getNumberOfSuccessfulCalls() + metrics.getNumberOfFailedCalls())
                    .withDetail("status", "Operational")
                    .build();
            } else if ("HALF_OPEN".equals(circuitBreakerState)) {
                return Health.up()
                    .withDetail("circuitBreakerState", circuitBreakerState)
                    .withDetail("status", "Recovering")
                    .withDetail("message", "Circuit breaker is testing recovery")
                    .build();
            } else {
                return Health.down()
                    .withDetail("circuitBreakerState", circuitBreakerState)
                    .withDetail("failureRate", String.format("%.2f%%", metrics.getFailureRate()))
                    .withDetail("status", "Degraded")
                    .withDetail("message", "Rate limiter using fallback strategy")
                    .build();
            }
        } catch (Exception e) {
            log.error("Error checking rate limiter health", e);
            return Health.down()
                .withDetail("error", e.getMessage())
                .withDetail("status", "Error")
                .build();
        }
    }
}
