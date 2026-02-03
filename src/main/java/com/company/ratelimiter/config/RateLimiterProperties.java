package com.company.ratelimiter.config;

import com.company.ratelimiter.model.RateLimitDimension;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for rate limiter.
 * Binds to 'ratelimiter' prefix in application.yml
 */
@Data
@Validated
@ConfigurationProperties(prefix = "ratelimiter")
public class RateLimiterProperties {

    /**
     * Enable/disable rate limiting globally
     */
    private boolean enabled = true;

    /**
     * Redis configuration
     */
    @Valid
    private RedisConfig redis = new RedisConfig();

    /**
     * Circuit breaker configuration
     */
    @Valid
    private CircuitBreakerConfig circuitBreaker = new CircuitBreakerConfig();

    /**
     * Rate limit rules
     */
    @Valid
    private List<RuleConfig> rules = new ArrayList<>();

    /**
     * HTTP header configuration
     */
    @Valid
    private HeaderConfig headers = new HeaderConfig();

    @Data
    public static class RedisConfig {
        private String host = "localhost";
        
        @Min(1)
        private int port = 6379;
        
        private String password;
        
        @NotNull
        private Duration timeout = Duration.ofMillis(100);
        
        @Valid
        private PoolConfig pool = new PoolConfig();
    }

    @Data
    public static class PoolConfig {
        @Min(1)
        private int maxActive = 50;
        
        @Min(0)
        private int maxIdle = 10;
        
        @Min(0)
        private int minIdle = 5;
        
        @NotNull
        private Duration maxWait = Duration.ofMillis(200);
    }

    @Data
    public static class CircuitBreakerConfig {
        private boolean enabled = true;
        
        @Min(0)
        @jakarta.validation.constraints.Max(100)
        private int failureRateThreshold = 50;
        
        @NotNull
        private Duration waitDurationInOpenState = Duration.ofSeconds(10);
        
        @Min(1)
        private int permittedCallsInHalfOpen = 5;
        
        @NotNull
        private FallbackStrategyType fallbackStrategy = FallbackStrategyType.LOCAL_CACHE;
    }

    public enum FallbackStrategyType {
        ALLOW_ALL,
        DENY_ALL,
        LOCAL_CACHE
    }

    @Data
    public static class RuleConfig {
        @NotNull
        private RateLimitDimension dimension;
        
        @Min(1)
        private long limit;
        
        @NotNull
        private Duration window;
        
        private int priority = 100;
        
        private boolean enabled = true;
        
        private String identifier;  // Optional: specific identifier
        
        private String description;
    }

    @Data
    public static class HeaderConfig {
        private boolean includeRateLimitHeaders = true;
        private boolean includeRetryAfter = true;
        
        private String limitHeader = "X-RateLimit-Limit";
        private String remainingHeader = "X-RateLimit-Remaining";
        private String resetHeader = "X-RateLimit-Reset";
        private String retryAfterHeader = "Retry-After";
    }
}
