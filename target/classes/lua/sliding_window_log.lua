-- Sliding Window Log Rate Limiting Algorithm
-- This script atomically checks and updates rate limit state in Redis
-- 
-- KEYS[1] = rate limit key (e.g., "ratelimit:user:12345:60")
-- ARGV[1] = limit (max requests allowed)
-- ARGV[2] = window size in milliseconds
-- ARGV[3] = request ID (UUID for uniqueness)
-- ARGV[4] = TTL in seconds
--
-- Returns: {allowed, remaining, reset_time}
--   allowed: 1 if allowed, 0 if denied
--   remaining: number of requests remaining in window
--   reset_time: Unix timestamp when window resets

-- Get current time from Redis server (avoids clock drift across app instances)
local current_time = redis.call('TIME')
local current_millis = tonumber(current_time[1]) * 1000 + math.floor(tonumber(current_time[2]) / 1000)

-- Parse arguments
local key = KEYS[1]
local limit = tonumber(ARGV[1])
local window_millis = tonumber(ARGV[2])
local request_id = ARGV[3]
local ttl_seconds = tonumber(ARGV[4])

-- Calculate window boundaries
local window_start = current_millis - window_millis

-- Remove expired entries (requests outside the sliding window)
-- This keeps memory usage bounded
redis.call('ZREMRANGEBYSCORE', key, 0, window_start)

-- Count current requests in the window
local current_count = redis.call('ZCARD', key)

-- Calculate when the window resets (in seconds)
local reset_time = math.floor((current_millis + window_millis) / 1000)

-- Check if limit is exceeded
if current_count < limit then
    -- ALLOWED: Add new request to sorted set
    redis.call('ZADD', key, current_millis, request_id)
    
    -- Set expiration to prevent memory leaks
    redis.call('EXPIRE', key, ttl_seconds)
    
    -- Return: [allowed=1, remaining, reset_time]
    return {1, limit - current_count - 1, reset_time}
else
    -- DENIED: Limit exceeded
    -- Return: [allowed=0, remaining=0, reset_time]
    return {0, 0, reset_time}
end
