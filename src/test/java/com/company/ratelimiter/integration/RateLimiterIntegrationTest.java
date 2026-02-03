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
import org.springframework.data.redis.core.RedisTemplate;
import lombok.extern.slf4j.Slf4j;

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
    "ratelimiter.redis.port=6380",
    "spring.redis.host=localhost",
    "spring.redis.port=6380"
})
@Slf4j
class RateLimiterIntegrationTest {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private RateLimitStrategyResolver strategyResolver;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setUp() {
        // Clear Redis data before each test
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        
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
        String uniqueId = "test-user-basic-" + System.currentTimeMillis();
        RateLimitContext context = RateLimitContext.builder()
            .userId(uniqueId)
            .ipAddress("192.168.1.1")
            .build();

        // First 10 requests should be allowed
        for (int i = 0; i < 10; i++) {
            // Create new context for each request to get unique requestId
            RateLimitContext iterContext = RateLimitContext.builder()
                .userId(uniqueId)
                .ipAddress("192.168.1.1")
                .build();
            RateLimitDecision decision = rateLimiterService.evaluateRateLimit(iterContext);
            assertThat(decision.isAllowed()).isTrue();
            assertThat(decision.getRemaining()).isEqualTo(10 - i - 1);
        }

        // 11th request should be denied (user limit = 10)
        RateLimitContext finalContext = RateLimitContext.builder()
            .userId(uniqueId)
            .ipAddress("192.168.1.1")
            .build();
        RateLimitDecision decision = rateLimiterService.evaluateRateLimit(finalContext);
        assertThat(decision.isAllowed()).isFalse();
        assertThat(decision.getDeniedBy()).isEqualTo(RateLimitDimension.USER);
    }

    @Test
    void testMultipleDimensions() {
        // Use timestamp-based unique identifier to avoid key conflicts
        String uniqueId = "test-user-" + System.currentTimeMillis();
        
        // User has limit of 10, IP has limit of 20
        // Should be limited by USER first (priority 1 < 2)
        
        for (int i = 0; i < 10; i++) {
            // Create a new context for each request to get a unique requestId
            RateLimitContext iterationContext = RateLimitContext.builder()
                .userId(uniqueId)
                .ipAddress("192.168.1.2")
                .build();
            
            RateLimitDecision decision = rateLimiterService.evaluateRateLimit(iterationContext);
            log.info("Request {}: allowed={}, remaining={}, fromFallback={}", i+1, decision.isAllowed(), decision.getRemaining(), decision.isFromFallback());
            assertThat(decision.isAllowed()).isTrue();
        }

        // 11th request with fresh context
        RateLimitContext finalContext = RateLimitContext.builder()
            .userId(uniqueId)
            .ipAddress("192.168.1.2")
            .build();
        
        RateLimitDecision decision = rateLimiterService.evaluateRateLimit(finalContext);
        log.info("Request 11: allowed={}, remaining={}, fromFallback={}", decision.isAllowed(), decision.getRemaining(), decision.isFromFallback());
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
            String userId = "user-" + i;
            String ipAddr = "192.168.1." + i;

            // Each user can make 10 requests
            for (int j = 0; j < 10; j++) {
                RateLimitContext context = RateLimitContext.builder()
                    .userId(userId)
                    .ipAddress(ipAddr)
                    .build();
                RateLimitDecision decision = rateLimiterService.evaluateRateLimit(context);
                assertThat(decision.isAllowed()).isTrue();
            }

            // 11th request denied for each user
            RateLimitContext context = RateLimitContext.builder()
                .userId(userId)
                .ipAddress(ipAddr)
                .build();
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
