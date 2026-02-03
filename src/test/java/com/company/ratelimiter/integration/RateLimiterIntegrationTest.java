package com.company.ratelimiter.integration;

import com.company.ratelimiter.RateLimiterApplication;
import com.company.ratelimiter.config.RateLimiterProperties;
import com.company.ratelimiter.core.RateLimitContext;
import com.company.ratelimiter.core.RateLimitDecision;
import com.company.ratelimiter.core.RateLimiterService;
import com.company.ratelimiter.model.RateLimitDimension;
import com.company.ratelimiter.model.RateLimitRule;
import com.company.ratelimiter.strategy.RateLimitStrategyResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for distributed rate limiter.
 * Tests the complete flow from context creation to Redis execution.
 * 
 * Note: Requires Redis running on localhost:6379
 */
@SpringBootTest(classes = RateLimiterApplication.class)
@TestPropertySource(properties = {
    "ratelimiter.enabled=true",
    "ratelimiter.redis.host=localhost",
    "ratelimiter.redis.port=6379",
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
class RateLimiterIntegrationTest {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private RateLimitStrategyResolver strategyResolver;

    @BeforeEach
    void setUp() {
        // Register test rules
        List<RateLimitRule> rules = List.of(
            RateLimitRule.builder()
                .dimension(RateLimitDimension.USER)
                .limit(10)
                .window(Duration.ofSeconds(60))
                .priority(1)
                .enabled(true)
                .build(),
            
            RateLimitRule.builder()
                .dimension(RateLimitDimension.IP)
                .limit(20)
                .window(Duration.ofSeconds(60))
                .priority(2)
                .enabled(true)
                .build()
        );
        
        strategyResolver.registerRules(rules);
    }

    @Test
    void testBasicRateLimiting() {
        RateLimitContext context = RateLimitContext.builder()
            .userId("test-user-1")
            .ipAddress("192.168.1.1")
            .build();

        // First 10 requests should be allowed
        for (int i = 0; i < 10; i++) {
            RateLimitDecision decision = rateLimiterService.evaluateRateLimit(context);
            assertThat(decision.isAllowed()).isTrue();
            assertThat(decision.getRemaining()).isEqualTo(10 - i - 1);
        }

        // 11th request should be denied (user limit = 10)
        RateLimitDecision decision = rateLimiterService.evaluateRateLimit(context);
        assertThat(decision.isAllowed()).isFalse();
        assertThat(decision.getDeniedBy()).isEqualTo(RateLimitDimension.USER);
    }

    @Test
    void testMultipleDimensions() {
        RateLimitContext context = RateLimitContext.builder()
            .userId("test-user-2")
            .ipAddress("192.168.1.2")
            .build();

        // User has limit of 10, IP has limit of 20
        // Should be limited by USER first (priority 1 < 2)
        
        for (int i = 0; i < 10; i++) {
            RateLimitDecision decision = rateLimiterService.evaluateRateLimit(context);
            assertThat(decision.isAllowed()).isTrue();
        }

        RateLimitDecision decision = rateLimiterService.evaluateRateLimit(context);
        assertThat(decision.isAllowed()).isFalse();
        assertThat(decision.getDeniedBy()).isEqualTo(RateLimitDimension.USER);
    }

    @Test
    void testConcurrentRequests() throws InterruptedException {
        String userId = "concurrent-user";
        int numThreads = 20;
        int requestsPerThread = 2;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger allowedCount = new AtomicInteger(0);
        AtomicInteger deniedCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        RateLimitContext context = RateLimitContext.builder()
                            .userId(userId)
                            .ipAddress("192.168.1.100")
                            .build();

                        RateLimitDecision decision = rateLimiterService.evaluateRateLimit(context);
                        
                        if (decision.isAllowed()) {
                            allowedCount.incrementAndGet();
                        } else {
                            deniedCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Exactly 10 should be allowed (user limit), rest denied
        assertThat(allowedCount.get()).isEqualTo(10);
        assertThat(deniedCount.get()).isEqualTo(numThreads * requestsPerThread - 10);
    }

    @Test
    void testDifferentUsers() {
        // Each user should have independent limits
        for (int i = 0; i < 5; i++) {
            RateLimitContext context = RateLimitContext.builder()
                .userId("user-" + i)
                .ipAddress("192.168.1." + i)
                .build();

            // Each user can make 10 requests
            for (int j = 0; j < 10; j++) {
                RateLimitDecision decision = rateLimiterService.evaluateRateLimit(context);
                assertThat(decision.isAllowed()).isTrue();
            }

            // 11th request denied for each user
            RateLimitDecision decision = rateLimiterService.evaluateRateLimit(context);
            assertThat(decision.isAllowed()).isFalse();
        }
    }

    @Test
    void testHealthCheck() {
        boolean isHealthy = rateLimiterService.isHealthy();
        assertThat(isHealthy).isTrue();
        
        String state = rateLimiterService.getCircuitBreakerState();
        assertThat(state).isEqualTo("CLOSED");
    }
}
