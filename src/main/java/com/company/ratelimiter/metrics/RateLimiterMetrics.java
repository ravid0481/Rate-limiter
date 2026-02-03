package com.company.ratelimiter.metrics;

import com.company.ratelimiter.core.RateLimiterService;
import com.company.ratelimiter.fallback.LocalCacheFallback;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * Exposes rate limiter metrics to Prometheus/Grafana.
 * Registers custom gauges for monitoring circuit breaker and cache state.
 */
@Slf4j
@Component
public class RateLimiterMetrics implements MeterBinder {

    private final RateLimiterService rateLimiterService;
    
    @Autowired(required = false)
    private LocalCacheFallback localCacheFallback;

    public RateLimiterMetrics(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        // Circuit breaker state as numeric value
        Gauge.builder("ratelimiter.circuitbreaker.state", rateLimiterService, service -> {
                String state = service.getCircuitBreakerState();
                return switch (state) {
                    case "CLOSED" -> 0;
                    case "OPEN" -> 1;
                    case "HALF_OPEN" -> 2;
                    default -> -1;
                };
            })
            .description("Circuit breaker state (0=CLOSED, 1=OPEN, 2=HALF_OPEN)")
            .register(registry);

        // Circuit breaker failure rate
        Gauge.builder("ratelimiter.circuitbreaker.failure.rate", rateLimiterService, service -> {
                try {
                    return service.getCircuitBreakerMetrics().getFailureRate();
                } catch (Exception e) {
                    return 0.0f;
                }
            })
            .description("Circuit breaker failure rate percentage")
            .baseUnit("percent")
            .register(registry);

        // Circuit breaker call metrics
        Gauge.builder("ratelimiter.circuitbreaker.calls.successful", rateLimiterService, service -> {
                try {
                    return service.getCircuitBreakerMetrics().getNumberOfSuccessfulCalls();
                } catch (Exception e) {
                    return 0;
                }
            })
            .description("Number of successful circuit breaker calls")
            .register(registry);

        Gauge.builder("ratelimiter.circuitbreaker.calls.failed", rateLimiterService, service -> {
                try {
                    return service.getCircuitBreakerMetrics().getNumberOfFailedCalls();
                } catch (Exception e) {
                    return 0;
                }
            })
            .description("Number of failed circuit breaker calls")
            .register(registry);

        // Health status
        Gauge.builder("ratelimiter.healthy", rateLimiterService, service -> service.isHealthy() ? 1 : 0)
            .description("Rate limiter health status (1=healthy, 0=unhealthy)")
            .register(registry);

        log.info("Rate limiter metrics registered with Micrometer");
    }
}
