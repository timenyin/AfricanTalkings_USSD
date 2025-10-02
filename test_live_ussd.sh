#!/bin/bash

NGROK_URL="https://cd7e5ae76b3f.ngrok-free.app"

echo "=== TESTING RESPONSE SPEED ==="
echo "Africa's Talking timeout: 10 seconds"
echo ""

# Test response time
echo "1. Testing Response Time:"
time curl -s -o /dev/null -w "Response Code: %{http_code}\nTotal Time: %{time_total}s\n" \
  "$NGROK_URL/ussd/health"
echo ""

# Test USSD endpoint speed
echo "2. Testing USSD Endpoint Speed:"
time curl -X POST "$NGROK_URL/ussd/fast-test" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=SPEED_TEST&phoneNumber=08100974728" \
  -s -o /dev/null -w "Response Code: %{http_code}\nTotal Time: %{time_total}s\n"
echo ""

# Test regular USSD
echo "3. Testing Regular USSD Speed:"
time curl -X POST "$NGROK_URL/ussd" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=ATUid_test123&phoneNumber=08100974728&serviceCode=*347*426%23&text=&networkCode=62130" \
  -s -o /dev/null -w "Response Code: %{http_code}\nTotal Time: %{time_total}s\n"

echo ""
echo "=== SPEED TEST COMPLETE ==="
echo "If response time > 5s, Africa's Talking will timeout!"