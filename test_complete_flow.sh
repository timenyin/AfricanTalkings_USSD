#!/bin/bash

echo "=== BALANCEÈ USSD COMPLETE SETUP TEST ==="

# Get the current ngrok URL (you'll need to update this manually)
NGROK_URL="https://2c0632e82e50.ngrok-free.app"

echo "Ngrok URL: $NGROK_URL"
echo "Test Phone: 08100974728"
echo ""

# Test 1: Health Check
echo "1. Testing Health Endpoint:"
curl -k -s "$NGROK_URL/ussd/health"
echo ""
echo ""

# Test 2: Complete USSD Flow
echo "2. Testing Complete USSD Flow:"

# Generate unique session ID
SESSION_ID="TEST_$(date +%s)"

echo "Session ID: $SESSION_ID"
echo ""

# Step 1: Initial dial
echo "Step 1 - Initial dial:"
curl -k -X POST "$NGROK_URL/ussd" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=${SESSION_ID}&phoneNumber=08100974728&serviceCode=*347*426#&text=&networkCode=62130"
echo ""
echo ""

# Step 2: Accept charges
echo "Step 2 - Accept charges:"
curl -k -X POST "$NGROK_URL/ussd" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=${SESSION_ID}&phoneNumber=08100974728&serviceCode=*347*426#&text=1&networkCode=62130"
echo ""
echo ""

# Step 3: Enter phone number
echo "Step 3 - Enter phone number:"
curl -k -X POST "$NGROK_URL/ussd" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=${SESSION_ID}&phoneNumber=08100974728&serviceCode=*347*426#&text=1*08100974728&networkCode=62130"
echo ""
echo ""

# Step 4: Continue to main menu
echo "Step 4 - Continue to main menu:"
curl -k -X POST "$NGROK_URL/ussd" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=${SESSION_ID}&phoneNumber=08100974728&serviceCode=*347*426#&text=1*08100974728*1&networkCode=62130"
echo ""
echo ""

# Step 5: Select Money Matters
echo "Step 5 - Select Money Matters:"
curl -k -X POST "$NGROK_URL/ussd" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=${SESSION_ID}&phoneNumber=08100974728&serviceCode=*347*426#&text=1*08100974728*1*2&networkCode=62130"
echo ""
echo ""

echo "=== TEST COMPLETE ==="