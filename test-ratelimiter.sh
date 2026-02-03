#!/bin/bash

# Distributed Rate Limiter - Demo Test Script
# This script demonstrates the rate limiter in action

set -e

echo "=================================================="
echo "   Distributed Rate Limiter - Test Script"
echo "=================================================="
echo ""

# Configuration
API_URL="${API_URL:-http://localhost:8080}"
ENDPOINT="$API_URL/api/demo/hello"
STATUS_ENDPOINT="$API_URL/api/demo/status"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test 1: Check if application is running
echo -e "${BLUE}[Test 1] Checking if application is running...${NC}"
if curl -s -f "$STATUS_ENDPOINT" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Application is running${NC}"
else
    echo -e "${RED}✗ Application is not running. Please start it with: mvn spring-boot:run${NC}"
    exit 1
fi
echo ""

# Test 2: Get rate limiter status
echo -e "${BLUE}[Test 2] Getting rate limiter status...${NC}"
STATUS=$(curl -s "$STATUS_ENDPOINT")
echo "$STATUS" | jq '.'
echo ""

# Test 3: Test basic rate limiting (user dimension)
echo -e "${BLUE}[Test 3] Testing basic rate limiting (10 requests per minute per user)...${NC}"
USER_ID="test-user-$(date +%s)"
ALLOWED=0
DENIED=0

for i in {1..15}; do
    RESPONSE=$(curl -s -w "\n%{http_code}" -H "X-User-Id: $USER_ID" "$ENDPOINT")
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    HEADERS=$(echo "$RESPONSE" | grep -i "x-ratelimit" || true)
    
    if [ "$HTTP_CODE" == "200" ]; then
        ((ALLOWED++))
        echo -e "${GREEN}[$i/15] ✓ Request allowed (200)${NC}"
        
        # Show rate limit headers
        if [ -n "$HEADERS" ]; then
            echo "$HEADERS" | while IFS= read -r line; do
                echo "        $line"
            done
        fi
    elif [ "$HTTP_CODE" == "429" ]; then
        ((DENIED++))
        echo -e "${RED}[$i/15] ✗ Rate limited (429)${NC}"
        
        # Show retry-after header
        RETRY_AFTER=$(echo "$RESPONSE" | grep -i "retry-after" || true)
        if [ -n "$RETRY_AFTER" ]; then
            echo "        $RETRY_AFTER"
        fi
    else
        echo -e "${YELLOW}[$i/15] ? Unexpected response ($HTTP_CODE)${NC}"
    fi
    
    # Small delay to simulate real traffic
    sleep 0.1
done

echo ""
echo -e "${BLUE}Results:${NC}"
echo -e "  Allowed: ${GREEN}$ALLOWED${NC}"
echo -e "  Denied:  ${RED}$DENIED${NC}"
echo ""

# Test 4: Test different users (independent limits)
echo -e "${BLUE}[Test 4] Testing independent limits for different users...${NC}"

for user_num in {1..3}; do
    USER_ID="user-$user_num-$(date +%s)"
    echo -e "${YELLOW}Testing user: $USER_ID${NC}"
    
    # Each user should be able to make 10 requests
    for i in {1..12}; do
        RESPONSE=$(curl -s -w "\n%{http_code}" -H "X-User-Id: $USER_ID" "$ENDPOINT")
        HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
        
        if [ "$HTTP_CODE" == "200" ]; then
            echo -n -e "${GREEN}.${NC}"
        elif [ "$HTTP_CODE" == "429" ]; then
            echo -n -e "${RED}X${NC}"
        fi
    done
    echo " (done)"
done
echo ""

# Test 5: Test IP-based rate limiting
echo -e "${BLUE}[Test 5] Testing IP-based rate limiting...${NC}"
echo "Making requests without user ID (rate limited by IP)..."

ALLOWED=0
DENIED=0

for i in {1..25}; do
    RESPONSE=$(curl -s -w "\n%{http_code}" "$ENDPOINT")
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    
    if [ "$HTTP_CODE" == "200" ]; then
        ((ALLOWED++))
        echo -n -e "${GREEN}.${NC}"
    elif [ "$HTTP_CODE" == "429" ]; then
        ((DENIED++))
        echo -n -e "${RED}X${NC}"
    fi
    
    sleep 0.05
done

echo ""
echo -e "Results: Allowed: ${GREEN}$ALLOWED${NC}, Denied: ${RED}$DENIED${NC}"
echo ""

# Test 6: Concurrent requests
echo -e "${BLUE}[Test 6] Testing concurrent requests...${NC}"
USER_ID="concurrent-user-$(date +%s)"
NUM_CONCURRENT=20

echo "Sending $NUM_CONCURRENT concurrent requests..."

# Create temporary file for results
TEMP_FILE=$(mktemp)

# Send concurrent requests
for i in $(seq 1 $NUM_CONCURRENT); do
    {
        RESPONSE=$(curl -s -w "\n%{http_code}" -H "X-User-Id: $USER_ID" "$ENDPOINT")
        HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
        echo "$HTTP_CODE" >> "$TEMP_FILE"
    } &
done

# Wait for all background jobs
wait

# Count results
ALLOWED=$(grep -c "200" "$TEMP_FILE" || echo 0)
DENIED=$(grep -c "429" "$TEMP_FILE" || echo 0)

echo -e "Results:"
echo -e "  Allowed: ${GREEN}$ALLOWED${NC} (should be exactly 10 due to user limit)"
echo -e "  Denied:  ${RED}$DENIED${NC}"

# Cleanup
rm "$TEMP_FILE"
echo ""

# Test 7: Check metrics
echo -e "${BLUE}[Test 7] Checking Prometheus metrics...${NC}"
METRICS=$(curl -s "$API_URL/actuator/prometheus" | grep "^ratelimiter_" | head -n 10)

if [ -n "$METRICS" ]; then
    echo -e "${GREEN}✓ Metrics are available${NC}"
    echo "Sample metrics:"
    echo "$METRICS"
else
    echo -e "${YELLOW}⚠ No metrics found${NC}"
fi
echo ""

# Test 8: Health check
echo -e "${BLUE}[Test 8] Final health check...${NC}"
HEALTH=$(curl -s "$API_URL/actuator/health/rateLimiter")
echo "$HEALTH" | jq '.'

HEALTH_STATUS=$(echo "$HEALTH" | jq -r '.status')
if [ "$HEALTH_STATUS" == "UP" ]; then
    echo -e "${GREEN}✓ Rate limiter is healthy${NC}"
else
    echo -e "${YELLOW}⚠ Rate limiter status: $HEALTH_STATUS${NC}"
fi
echo ""

echo "=================================================="
echo "   Test Complete!"
echo "=================================================="
echo ""
echo "Next steps:"
echo "  1. Check Grafana dashboard: http://localhost:3000"
echo "  2. Check Prometheus: http://localhost:9090"
echo "  3. View health endpoint: $API_URL/actuator/health"
echo "  4. View metrics: $API_URL/actuator/prometheus"
echo ""
