package com.company.ratelimiter.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Context object that holds all information needed for rate limiting a request.
 * Extracted from HTTP request and passed through the rate limiting pipeline.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitContext {
    
    /**
     * Unique request ID for tracking and deduplication
     */
    @Builder.Default
    private String requestId = UUID.randomUUID().toString();
    
    /**
     * User ID (if authenticated)
     */
    private String userId;
    
    /**
     * Client IP address
     */
    private String ipAddress;
    
    /**
     * API key (if using API key authentication)
     */
    private String apiKey;
    
    /**
     * Tenant/Organization ID (for multi-tenant systems)
     */
    private String tenantId;
    
    /**
     * HTTP method (GET, POST, etc.)
     */
    private String httpMethod;
    
    /**
     * Request URI/endpoint
     */
    private String requestUri;
    
    /**
     * Additional attributes that can be used for custom rate limiting logic
     */
    @Builder.Default
    private Map<String, String> attributes = new HashMap<>();

    /**
     * Get identifier for a specific dimension
     */
    public String getIdentifier(com.company.ratelimiter.model.RateLimitDimension dimension) {
        return switch (dimension) {
            case USER -> userId;
            case IP -> ipAddress;
            case API_KEY -> apiKey;
            case TENANT -> tenantId;
            case ENDPOINT -> requestUri;
            case GLOBAL -> "global";
        };
    }

    /**
     * Check if this context has an identifier for the given dimension
     */
    public boolean hasIdentifier(com.company.ratelimiter.model.RateLimitDimension dimension) {
        String identifier = getIdentifier(dimension);
        return identifier != null && !identifier.isEmpty();
    }

    /**
     * Add custom attribute
     */
    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    /**
     * Get custom attribute
     */
    public String getAttribute(String key) {
        return this.attributes.get(key);
    }
}
