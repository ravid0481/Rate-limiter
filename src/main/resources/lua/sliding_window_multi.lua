-- Multi-Dimension Sliding Window Log Rate Limiting
-- This script atomically checks ALL applicable rate limits before incrementing ANY counters
-- This prevents partial increments when multiple dimensions are being checked
--
-- KEYS[1..N] = rate limit keys (one per dimension)
-- ARGV[1] = number of dimensions
-- ARGV[2..N] = triplets of (limit, window_millis, ttl_seconds) for each dimension
-- ARGV[last] = request_id (UUID)
--
-- Returns: {allowed, failed_dimension_index, remaining, reset_time}
--   allowed: 1 if all limits pass, 0 if any limit fails
--   failed_dimension_index: index of first failed dimension (0 if all pass)
--   remaining: minimum remaining across all dimensions
--   reset_time: earliest reset time across all dimensions

-- Get current time from Redis
local current_time = redis.call('TIME')
local current_millis = tonumber(current_time[1]) * 1000 + math.floor(tonumber(current_time[2]) / 1000)

-- Parse number of dimensions
local num_dimensions = tonumber(ARGV[1])
local request_id = ARGV[#ARGV]

-- First pass: CHECK all limits without modifying anything
local min_remaining = -1
local earliest_reset = current_millis + 86400000 -- 24 hours from now

for i = 1, num_dimensions do
    local key = KEYS[i]
    
    -- Calculate argument positions (limit, window_millis, ttl_seconds)
    local arg_base = 2 + (i - 1) * 3
    local limit = tonumber(ARGV[arg_base])
    local window_millis = tonumber(ARGV[arg_base + 1])
    local ttl_seconds = tonumber(ARGV[arg_base + 2])
    
    local window_start = current_millis - window_millis
    
    -- Remove expired entries
    redis.call('ZREMRANGEBYSCORE', key, 0, window_start)
    
    -- Count current requests
    local current_count = redis.call('ZCARD', key)
    
    -- Calculate reset time for this dimension
    local reset_time = math.floor((current_millis + window_millis) / 1000)
    
    -- Track earliest reset time
    if reset_time < earliest_reset then
        earliest_reset = reset_time
    end
    
    -- Check if this dimension's limit is exceeded
    if current_count >= limit then
        -- DENIED: Return immediately without incrementing anything
        return {0, i, 0, reset_time}
    end
    
    -- Track minimum remaining
    local remaining = limit - current_count - 1
    if min_remaining == -1 or remaining < min_remaining then
        min_remaining = remaining
    end
end

-- Second pass: ALL limits passed - now increment ALL counters
for i = 1, num_dimensions do
    local key = KEYS[i]
    
    -- Calculate argument positions
    local arg_base = 2 + (i - 1) * 3
    local ttl_seconds = tonumber(ARGV[arg_base + 2])
    
    -- Add request to this dimension
    redis.call('ZADD', key, current_millis, request_id .. ':' .. i)
    
    -- Set expiration
    redis.call('EXPIRE', key, ttl_seconds)
end

-- ALLOWED: All limits passed and all counters incremented
return {1, 0, min_remaining, math.floor(earliest_reset / 1000)}
