package com.company.ratelimiter.filter;

import com.company.ratelimiter.config.RateLimiterProperties;
import com.company.ratelimiter.core.RateLimitContext;
import com.company.ratelimiter.core.RateLimitDecision;
import com.company.ratelimiter.core.RateLimiterService;
import com.company.ratelimiter.exception.RateLimitExceededException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Servlet filter that intercepts HTTP requests and applies rate limiting.
 * Runs early in the filter chain to protect downstream services.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)  // Run early, but after security filters
@ConditionalOnProperty(
    prefix = "ratelimiter",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class RateLimiterFilter implements Filter {

    private final RateLimiterService rateLimiterService;
    private final RateLimiterProperties properties;

    public RateLimiterFilter(
            RateLimiterService rateLimiterService,
            RateLimiterProperties properties) {
        this.rateLimiterService = rateLimiterService;
        this.properties = properties;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Build rate limit context from request
        RateLimitContext context = buildContext(httpRequest);

        try {
            // Check rate limit
            rateLimiterService.checkRateLimit(context);

            // Get decision for headers (don't throw exception)
            RateLimitDecision decision = rateLimiterService.evaluateRateLimit(context);
            
            // Add rate limit headers to response
            if (properties.getHeaders().isIncludeRateLimitHeaders()) {
                addRateLimitHeaders(httpResponse, decision);
            }

            // Continue filter chain
            chain.doFilter(request, response);

        } catch (RateLimitExceededException e) {
            // Rate limit exceeded - return 429
            handleRateLimitExceeded(httpResponse, e);
        }
    }

    /**
     * Build rate limit context from HTTP request
     */
    private RateLimitContext buildContext(HttpServletRequest request) {
        return RateLimitContext.builder()
            .userId(extractUserId(request))
            .ipAddress(extractClientIp(request))
            .apiKey(extractApiKey(request))
            .tenantId(extractTenantId(request))
            .httpMethod(request.getMethod())
            .requestUri(request.getRequestURI())
            .build();
    }

    /**
     * Extract user ID from request (authenticated user)
     * Override this method to integrate with your authentication system
     */
    protected String extractUserId(HttpServletRequest request) {
        // Option 1: From Principal
        if (request.getUserPrincipal() != null) {
            return request.getUserPrincipal().getName();
        }
        
        // Option 2: From custom header
        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        
        // Option 3: From JWT token (implement your own logic)
        // String jwt = request.getHeader("Authorization");
        // return extractUserIdFromJwt(jwt);
        
        return null;
    }

    /**
     * Extract client IP address
     * Handles X-Forwarded-For header for proxied requests
     */
    protected String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Extract API key from request
     */
    protected String extractApiKey(HttpServletRequest request) {
        // Option 1: From Authorization header
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("ApiKey ")) {
            return authorization.substring(7);
        }
        
        // Option 2: From custom header
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && !apiKey.isEmpty()) {
            return apiKey;
        }
        
        // Option 3: From query parameter (less secure, not recommended for production)
        // return request.getParameter("api_key");
        
        return null;
    }

    /**
     * Extract tenant ID from request (for multi-tenant systems)
     */
    protected String extractTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader("X-Tenant-Id");
        if (tenantId != null && !tenantId.isEmpty()) {
            return tenantId;
        }
        
        // Could also extract from subdomain, path, or JWT token
        return null;
    }

    /**
     * Add rate limit headers to response
     */
    private void addRateLimitHeaders(HttpServletResponse response, RateLimitDecision decision) {
        RateLimiterProperties.HeaderConfig headerConfig = properties.getHeaders();
        
        if (decision.getLimit() > 0) {
            response.setHeader(headerConfig.getLimitHeader(), String.valueOf(decision.getLimit()));
            response.setHeader(headerConfig.getRemainingHeader(), String.valueOf(decision.getRemaining()));
            response.setHeader(headerConfig.getResetHeader(), String.valueOf(decision.getResetTime()));
        }
    }

    /**
     * Handle rate limit exceeded - return 429 response
     */
    private void handleRateLimitExceeded(
            HttpServletResponse response,
            RateLimitExceededException e) throws IOException {

        RateLimitDecision decision = e.getDecision();
        
        // Set status code
        response.setStatus(429);  // SC_TOO_MANY_REQUESTS
        
        // Add rate limit headers
        if (properties.getHeaders().isIncludeRateLimitHeaders()) {
            RateLimiterProperties.HeaderConfig headerConfig = properties.getHeaders();
            
            response.setHeader(headerConfig.getLimitHeader(), String.valueOf(decision.getLimit()));
            response.setHeader(headerConfig.getRemainingHeader(), "0");
            response.setHeader(headerConfig.getResetHeader(), String.valueOf(decision.getResetTime()));
            
            if (properties.getHeaders().isIncludeRetryAfter()) {
                response.setHeader(headerConfig.getRetryAfterHeader(), 
                    String.valueOf(decision.getRetryAfterSeconds()));
            }
        }
        
        // Write error response body
        response.setContentType("application/json");
        String errorBody = String.format(
            "{\"error\":\"Rate limit exceeded\",\"message\":\"%s\",\"retryAfter\":%d}",
            e.getMessage(),
            decision.getRetryAfterSeconds()
        );
        response.getWriter().write(errorBody);
        response.getWriter().flush();
        
        log.warn("Rate limit exceeded: {}", e.getMessage());
    }
}
