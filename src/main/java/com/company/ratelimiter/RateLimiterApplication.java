package com.company.ratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application for Distributed Rate Limiter.
 * 
 * This application demonstrates a production-grade distributed rate limiter using:
 * - Redis for centralized state
 * - Sliding Window Log algorithm for accurate rate limiting
 * - Circuit breaker for resilience
 * - Multiple fallback strategies
 * - Comprehensive monitoring and health checks
 * 
 * The rate limiter can handle:
 * - Multiple dimensions (USER, IP, API_KEY, TENANT, ENDPOINT, GLOBAL)
 * - Distributed deployment across multiple instances
 * - High throughput (100K+ req/sec)
 * - Low latency (P95 < 5ms, P99 < 10ms)
 * - Redis failures with graceful degradation
 */
@SpringBootApplication
public class RateLimiterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateLimiterApplication.class, args);
    }
}
