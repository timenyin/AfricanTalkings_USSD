#!/bin/bash

NGROK_URL="https://cd7e5ae76b3f.ngrok-free.app"

echo "=== TESTING PUBLIC ACCESSIBILITY ==="
echo "Testing URL: $NGROK_URL"
echo ""

# Test 1: Basic connectivity
echo "1. Testing Basic Connectivity:"
curl -s -o /dev/null -w "HTTP Status: %{http_code}\n" "$NGROK_URL/ussd/ping"
echo ""

# Test 2: Test from different location (using external service)
echo "2. Testing from External Network:"
curl -s -X POST "https://httpbin.org/post" \
  -H "Content-Type: application/json" \
  -d "{\"test_url\": \"$NGROK_URL/ussd/ping\"}"
echo ""
echo ""

# Test 3: Test with Africa's Talking specific user agent
echo "3. Testing with Africa's Talking Simulation:"
curl -s -X POST "$NGROK_URL/ussd" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "User-Agent: Africa's Talking" \
  -d "sessionId=ATUid_test123&phoneNumber=+2348100974728&serviceCode=*347*426%23&text=&networkCode=62130" \
  -w "Response Time: %{time_total}s\nHTTP Status: %{http_code}\n"
echo ""

echo "=== ACCESSIBILITY TEST COMPLETE ==="