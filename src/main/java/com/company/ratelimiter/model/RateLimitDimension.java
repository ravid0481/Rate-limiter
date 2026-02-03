package com.company.ratelimiter.model;

/**
 * Defines the dimension (scope) of rate limiting.
 * Each dimension represents a different way to identify and limit requests.
 */
public enum RateLimitDimension {
    /**
     * Rate limit per authenticated user ID
     */
    USER("user"),
    
    /**
     * Rate limit per client IP address
     */
    IP("ip"),
    
    /**
     * Rate limit per API key
     */
    API_KEY("apikey"),
    
    /**
     * Rate limit per specific API endpoint
     */
    ENDPOINT("endpoint"),
    
    /**
     * Rate limit per tenant/organization
     */
    TENANT("tenant"),
    
    /**
     * Global rate limit (applies to all requests)
     */
    GLOBAL("global");

    private final String value;

    RateLimitDimension(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
