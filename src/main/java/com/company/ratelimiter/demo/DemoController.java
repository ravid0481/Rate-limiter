package com.company.ratelimiter.demo;

import com.company.ratelimiter.core.RateLimiterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Demo REST controller to test rate limiting.
 * 
 * Test endpoints:
 * - GET  /api/demo/hello - Simple endpoint (rate limited)
 * - GET  /api/demo/status - Rate limiter status
 * - POST /api/demo/test - Test endpoint with response
 */
@Slf4j
@RestController
@RequestMapping("/api/demo")
@ConditionalOnProperty(prefix = "demo", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DemoController {

    private final RateLimiterService rateLimiterService;

    public DemoController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    /**
     * Simple hello endpoint (rate limited automatically by filter)
     */
    @GetMapping("/hello")
    public ResponseEntity<Map<String, Object>> hello(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from rate-limited API!");
        response.put("timestamp", Instant.now().toString());
        response.put("userId", userId != null ? userId : "anonymous");
        
        log.info("Processed request for user: {}", userId != null ? userId : "anonymous");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint with custom response
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> test(
            @RequestBody(required = false) Map<String, Object> body) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Request processed successfully");
        response.put("timestamp", Instant.now().toString());
        response.put("receivedData", body);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get rate limiter status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> status = new HashMap<>();
        status.put("healthy", rateLimiterService.isHealthy());
        status.put("circuitBreakerState", rateLimiterService.getCircuitBreakerState());
        
        var metrics = rateLimiterService.getCircuitBreakerMetrics();
        Map<String, Object> metricsMap = new HashMap<>();
        metricsMap.put("failureRate", String.format("%.2f%%", metrics.getFailureRate()));
        metricsMap.put("successfulCalls", metrics.getNumberOfSuccessfulCalls());
        metricsMap.put("failedCalls", metrics.getNumberOfFailedCalls());
        
        status.put("metrics", metricsMap);
        status.put("timestamp", Instant.now().toString());
        
        return ResponseEntity.ok(status);
    }

    /**
     * Simulate heavy computation (for load testing)
     */
    @GetMapping("/heavy")
    public ResponseEntity<Map<String, Object>> heavyComputation(
            @RequestParam(defaultValue = "100") int duration) {
        
        try {
            Thread.sleep(Math.min(duration, 5000));  // Max 5 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Heavy computation completed");
        response.put("duration", duration + "ms");
        response.put("timestamp", Instant.now().toString());
        
        return ResponseEntity.ok(response);
    }
}
