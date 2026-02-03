package com.company.ratelimiter.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis configuration for rate limiter.
 * Configures connection pool, timeouts, and RedisTemplate.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "ratelimiter", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedisConfiguration {

    private final RateLimiterProperties properties;

    public RedisConfiguration(RateLimiterProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RateLimiterProperties.RedisConfig redisConfig = properties.getRedis();
        RateLimiterProperties.PoolConfig poolConfig = redisConfig.getPool();

        // Redis server configuration
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration();
        serverConfig.setHostName(redisConfig.getHost());
        serverConfig.setPort(redisConfig.getPort());
        
        if (redisConfig.getPassword() != null && !redisConfig.getPassword().isEmpty()) {
            serverConfig.setPassword(redisConfig.getPassword());
        }

        // Socket options for connection timeout
        SocketOptions socketOptions = SocketOptions.builder()
            .connectTimeout(redisConfig.getTimeout())
            .build();

        // Client options
        ClientOptions clientOptions = ClientOptions.builder()
            .socketOptions(socketOptions)
            .build();

        // Pooling configuration
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
            .commandTimeout(redisConfig.getTimeout())
            .clientOptions(clientOptions)
            .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(serverConfig, clientConfig);
        
        log.info("Redis connection factory configured: {}:{}", 
            redisConfig.getHost(), redisConfig.getPort());
        
        return factory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Use String serializers for keys and values
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);
        
        template.afterPropertiesSet();
        
        log.info("RedisTemplate configured for rate limiter");
        return template;
    }
}
