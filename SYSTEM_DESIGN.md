# System Design Document: Distributed Rate Limiter

> **Interview-Ready Design Decisions and Trade-offs**

This document explains the key design decisions made in this distributed rate limiter implementation. These are the answers you'd give in a system design interview.

---

## 1. Core Algorithm Choice: Sliding Window Log

### Why Not Token Bucket?

**Interviewer Question**: "Why did you choose Sliding Window Log over Token Bucket?"

**Answer**:
```
I chose Sliding Window Log for three critical reasons:

1. ACCURACY: Token Bucket allows bursts up to the bucket size, which can 
   overwhelm downstream services. At API Gateway scale, we need strict enforcement.
   
2. DISTRIBUTED CONSISTENCY: Sliding Window Log uses Redis sorted sets, which provide
   atomic operations. Token Bucket requires coordinating token refills across instances,
   adding complexity.
   
3. OBSERVABILITY: With Sliding Window Log, I can inspect exact request patterns in
   Redis. This is invaluable for debugging production issues.

The trade-off is higher memory usage (storing timestamps vs. just a counter), but
at ~100 bytes per request × 100 req/min × 1M users = ~10GB worst-case, which is
acceptable for modern Redis Cluster deployments.
```

### Memory Estimation

```
Per-request overhead: ~100 bytes (UUID + timestamp + Redis overhead)
Window size: 60 seconds
Rate limit: 100 requests/minute

Per user memory = 100 requests × 100 bytes = 10 KB
1M active users = 10 GB worst-case
Actual usage is much lower due to:
  - TTL eviction (removes old entries)
  - Not all users hit limits simultaneously
  - Active user percentage is typically <10%
```

---

## 2. Multi-Dimension Atomic Check

### The Partial Increment Problem

**Interviewer Question**: "What happens if you check USER and IP limits separately?"

**Answer**:
```
There's a subtle race condition:

BAD APPROACH (separate checks):
1. Check USER limit → PASS (increment USER counter)
2. Check IP limit → FAIL
3. Result: USER counter incremented but request denied
4. Next request: USER counter is wrong!

CORRECT APPROACH (single Lua script):
1. Check ALL limits first
2. If ANY fails → return DENIED, increment NOTHING
3. If ALL pass → increment ALL counters atomically

This is why I use sliding_window_multi.lua that checks all dimensions in one
atomic Redis operation. The key insight is: "read all, decide once, write all."
```

### Code Evidence

See `src/main/resources/lua/sliding_window_multi.lua`:
```lua
-- First pass: CHECK all limits without modifying
for i = 1, num_dimensions do
    if current_count >= limit then
        return {0, i, 0, reset_time}  -- DENY without incrementing
    end
end

-- Second pass: ALL passed - increment ALL
for i = 1, num_dimensions do
    redis.call('ZADD', key, current_millis, request_id)
end
```

---

## 3. Clock Drift Solution

**Interviewer Question**: "How do you handle clock drift between application instances?"

**Answer**:
```
Clock drift is a classic distributed systems problem. Here's my solution:

PROBLEM: If App1 clock is 10 seconds ahead of App2, timestamps are inconsistent.

SOLUTION: Use Redis server time (TIME command) as the authoritative source.

In the Lua script:
  local current_time = redis.call('TIME')
  local current_millis = tonumber(current_time[1]) * 1000 + ...

This way, ALL instances use the same clock (Redis), eliminating drift.

Alternative approaches I rejected:
  - NTP sync: Not reliable enough for sub-second accuracy
  - Logical clocks (Lamport): Adds complexity, not needed here
  - System.currentTimeMillis(): Different on each instance
```

---

## 4. Redis Cluster and Hot Keys

**Interviewer Question**: "How does this work with Redis Cluster?"

**Answer**:
```
IMPORTANT NUANCE: Redis Cluster distributes *keys*, not *members within a key*.

A single rate limit key like "ratelimit:user:12345:60" is a ZSET that lives on
one shard. If a celebrity user generates 1M requests, that ZSET still lives on
one Redis node.

SOLUTION HIERARCHY:
1. Normal case: Redis handles millions of ops/sec per key → no problem
2. Hot key (celebrity): Single Redis node becomes CPU bottleneck
3. Mitigation: Application-level sharding

Application-level sharding example:
  hash(userId) % 10 → shard_id
  Key becomes: "ratelimit:user:12345:60:shard_3"
  
  Now the load is distributed across 10 keys, each on different shards.

I mention this in interviews because it shows I understand Redis Cluster internals,
not just "throw it in a cluster and it scales."
```

---

## 5. Circuit Breaker Design

**Interviewer Question**: "What happens when Redis goes down?"

**Answer**:
```
I implement a circuit breaker with three states:

CLOSED (Normal):
  - All requests go to Redis
  - Success rate monitored
  
OPEN (Redis down):
  - Fast-fail without calling Redis
  - Fallback strategy activates:
      a) LOCAL_CACHE: Per-instance rate limiting (eventual consistency)
      b) ALLOW_ALL: Optimistic (prioritize availability)
      c) DENY_ALL: Pessimistic (prioritize data protection)
  
HALF_OPEN (Testing recovery):
  - Allow limited test requests to Redis
  - If success → transition to CLOSED
  - If failure → back to OPEN

Configuration:
  failure-rate-threshold: 50%  (open after 50% failures)
  wait-duration: 10s           (test recovery after 10s)
  
I default to LOCAL_CACHE fallback because it provides:
  - Some rate limiting (better than nothing)
  - Eventual consistency (okay for most use cases)
  - Better UX than DENY_ALL during outages
```

### Latency During Outage

**Question**: "What if Redis is slow but not down?"

**Answer**:
```
Latency spikes trigger the circuit breaker before total failure.

Resilience4j monitors:
  - Slow call percentage (calls >100ms)
  - If >50% slow → OPEN circuit
  - This prevents cascading failures

During HALF_OPEN:
  - Only 5 test calls allowed
  - If they succeed quickly → CLOSED
  - This prevents "flapping" between states
```

---

## 6. Performance Optimization

**Interviewer Question**: "How do you achieve P95 < 5ms latency?"

**Answer**:
```
Five key optimizations:

1. LUA SCRIPTS (server-side execution):
   - Single network round-trip
   - Atomic operations
   - No data transfer until final result
   
2. CONNECTION POOLING:
   - Lettuce connection pool (50 max connections)
   - Reuse connections across requests
   - Avoids handshake overhead
   
3. SCRIPT CACHING:
   - Load Lua scripts once at startup
   - Redis keeps SHA hash
   - Subsequent calls use EVALSHA (saves bandwidth)
   
4. LOCAL CONFIG CACHE:
   - Rate limit rules cached in Caffeine (60s TTL)
   - Avoids Redis lookup per request
   - Async refresh prevents blocking
   
5. PIPELINING (future enhancement):
   - Batch multiple checks into one round-trip
   - Useful for multi-endpoint checks

LATENCY BREAKDOWN:
  Network RTT: 1-2ms (same AZ)
  Redis execution: <1ms (in-memory, scripted)
  Java overhead: <1ms (connection pool, serialization)
  Total: P95 ~3-4ms, P99 ~7-9ms
```

---

## 7. Scale Estimation

**Interviewer Question**: "Can this handle 1M requests per second?"

**Answer**:
```
Let's do the math:

SINGLE REDIS INSTANCE:
  - ~100K ops/sec (conservative)
  - Each rate limit check = 1 Lua script execution = 1 op
  - Theoretical max: 100K requests/sec per instance

REDIS CLUSTER (10 shards):
  - 100K × 10 = 1M ops/sec
  - With proper key distribution (hash slots)

APPLICATION LAYER:
  - Stateless → horizontally scalable
  - Each instance handles ~10K req/sec
  - 100 instances = 1M req/sec

NETWORK:
  - 1M req/sec × 1KB avg = 1 GB/sec
  - 10 Gbps NIC handles easily

BOTTLENECKS AT 1M RPS:
  1. Hot keys (celebrity users) → app-level sharding
  2. Redis network bandwidth → Redis Cluster
  3. Single Redis node CPU → distribute hot keys

OPTIMIZATIONS FOR >1M RPS:
  1. Client-side caching (read-through for hot keys)
  2. Batching (group checks together)
  3. Probabilistic algorithms for extreme scale (leaky bucket with smoothing)
```

---

## 8. Security Considerations

**Interviewer Question**: "How do you prevent abuse of this rate limiter?"

**Answer**:
```
1. IDENTIFIER EXTRACTION:
   - userId: Extract from verified JWT (not from headers users can forge)
   - IP: Use X-Forwarded-For BUT validate (prevent header injection)
   - API key: Hash before storing in Redis (prevent key enumeration)

2. DDOS PROTECTION:
   - Global rate limit (last resort)
   - IP-based limiting catches distributed attacks
   - Shadow mode: Log only (test rules before enforcing)

3. REDIS SECURITY:
   - AUTH enabled (password authentication)
   - TLS for data in transit
   - Network isolation (private subnet)
   - No sensitive data in keys (use hashed identifiers)

4. RATE LIMIT BYPASSES:
   - Priority rules for specific users (identifier-based)
   - Separate Redis instance for critical services
   - Emergency override flag (ops can disable rate limiting)
```

---

## 9. Monitoring Strategy

**Interviewer Question**: "How do you monitor this in production?"

**Answer**:
```
Four monitoring pillars:

1. GOLDEN SIGNALS (SLIs):
   - Latency: P50/P95/P99 of rate limit checks
   - Traffic: Requests/sec (allowed vs denied)
   - Errors: Circuit breaker opens, Redis failures
   - Saturation: Redis CPU/memory, connection pool usage

2. ALERTING (SLOs):
   - P99 latency > 10ms for 5 minutes → page
   - Circuit breaker OPEN → page
   - Denial rate > 20% → investigate
   - Redis memory > 80% → warning

3. DASHBOARDS:
   - Real-time: Request rate, denial rate, latency
   - Circuit breaker: State timeline, transition events
   - Redis: Ops/sec, memory usage, key count
   - Business: Top denied users, denial reasons

4. TRACING:
   - Distributed tracing (Zipkin/Jaeger)
   - Trace each request through: filter → service → executor → Redis
   - Correlate slow requests with Redis latency spikes

RUNBOOK EXAMPLE:
  Alert: "Circuit Breaker OPEN"
  1. Check Redis health: redis-cli PING
  2. Check network: ping redis-host
  3. Check metrics: Are other services affected?
  4. Mitigation: Traffic continues via LOCAL_CACHE fallback
  5. Fix: Restore Redis or wait for auto-recovery (10s)
```

---

## 10. Testing Strategy

**Interviewer Question**: "How do you test a distributed rate limiter?"

**Answer**:
```
Five test levels:

1. UNIT TESTS:
   - Lua script logic (load/parse/validate)
   - Decision logic (allowed/denied/remaining)
   - Context extraction (userId, IP, API key)

2. INTEGRATION TESTS:
   - Embedded Redis (test-containers)
   - End-to-end flow: HTTP → Filter → Service → Redis
   - Multi-dimension checks (USER + IP)

3. LOAD TESTS:
   - JMeter: Ramp from 100 → 10K req/sec
   - Verify P99 latency stays <10ms
   - Check Redis memory growth (should plateau with TTL)

4. CHAOS TESTS:
   - Kill Redis mid-request → circuit breaker should open
   - Network partition → fallback strategy activates
   - Clock skew → verify Redis TIME is used

5. PRODUCTION TESTS:
   - Shadow mode: Log rate limit decisions without enforcing
   - A/B test: 1% traffic rate limited, compare metrics
   - Canary: Deploy to 1 instance, monitor before rollout

TEST MATRIX:
  - Concurrent requests (race conditions)
  - Different users (isolation)
  - Window boundaries (no burst leakage)
  - Redis failures (resilience)
  - High load (performance)
```

---

## Key Interview Talking Points

When presenting this design in an interview:

1. **Start with the problem**: "We need distributed rate limiting across N instances with strict accuracy and <10ms latency"

2. **Explain the algorithm choice**: "I chose Sliding Window Log over Token Bucket because..."

3. **Address consistency**: "The key insight is atomic multi-dimension checks in a single Lua script"

4. **Show you've operated systems**: "I added circuit breakers because Redis will fail, and we need graceful degradation"

5. **Demonstrate scale thinking**: "At 1M RPS, hot keys become an issue, so we'd shard at the application level"

6. **Mention observability**: "I export Prometheus metrics because you can't operate what you can't measure"

7. **Discuss trade-offs**: "Higher memory usage is acceptable because accuracy is more important than memory cost"

---

## Production Readiness Checklist

✅ **Correctness**
- Atomic operations (no race conditions)
- Clock drift handled (Redis TIME)
- Multi-dimension consistency (Lua script)

✅ **Performance**
- P95 < 5ms (Lua scripts, connection pooling)
- Scales to 100K+ RPS (Redis Cluster)
- Memory bounded (TTL eviction)

✅ **Reliability**
- Circuit breaker (handles Redis failures)
- Fallback strategies (ALLOW_ALL, DENY_ALL, LOCAL_CACHE)
- Health checks (Spring Actuator)

✅ **Observability**
- Metrics (Prometheus)
- Logs (structured logging)
- Tracing (distributed tracing ready)
- Health endpoint (circuit breaker state)

✅ **Security**
- Redis AUTH + TLS
- Identifier validation
- No sensitive data in keys

✅ **Operations**
- Configuration hot-reload
- Shadow mode support
- Runbook for common issues
- Zero-downtime deployment (stateless)

---

This design is production-ready and interview-safe. You can confidently defend every decision.
