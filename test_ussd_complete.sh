#!/bin/bash

echo "=== BALANCEÈ USSD COMPLETE TEST ==="

# Current ngrok URL - UPDATE THIS WITH YOUR ACTUAL NGROK URL
NGROK_URL="https://a5a66a4d3f4a.ngrok-free.app"

echo "Testing with Ngrok URL: $NGROK_URL"
echo "Test Phone: 08100974728"

# Test health endpoint
echo ""
echo "1. Health Check:"
curl -k -s $NGROK_URL/ussd/health | python -m json.tool

# Test config endpoint
echo ""
echo "2. Config Check:"
curl -k -s $NGROK_URL/ussd/config | python -m json.tool

# Test simple endpoint
echo ""
echo "3. Simple Test:"
curl -k -s $NGROK_URL/ussd/test | python -m json.tool

# Complete USSD Flow Test
echo ""
echo "4. COMPLETE USSD FLOW TEST:"

# Step 1: Initial USSD Dial
echo ""
echo "Step 1: Initial Dial"
SESSION_ID="TEST_$(date +%s)"
RESPONSE1=$(curl -k -s -X POST $NGROK_URL/ussd \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=${SESSION_ID}&phoneNumber=08100974728&serviceCode=*347*426#&text=&networkCode=62130")
echo "Response: $RESPONSE1"

# Step 2: Accept charges
echo ""
echo "Step 2: Accept charges (Input: 1)"
RESPONSE2=$(curl -k -s -X POST $NGROK_URL/ussd \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=${SESSION_ID}&phoneNumber=08100974728&serviceCode=*347*426#&text=1&networkCode=62130")
echo "Response: $RESPONSE2"

# Step 3: Enter phone number
echo ""
echo "Step 3: Enter phone number (Input: 1*08100974728)"
RESPONSE3=$(curl -k -s -X POST $NGROK_URL/ussd \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=${SESSION_ID}&phoneNumber=08100974728&serviceCode=*347*426#&text=1*08100974728&networkCode=62130")
echo "Response: $RESPONSE3"

# Step 4: Continue to main menu
echo ""
echo "Step 4: Continue to main menu (Input: 1*08100974728*1)"
RESPONSE4=$(curl -k -s -X POST $NGROK_URL/ussd \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=${SESSION_ID}&phoneNumber=08100974728&serviceCode=*347*426#&text=1*08100974728*1&networkCode=62130")
echo "Response: $RESPONSE4"

# Step 5: Select Money Matters
echo ""
echo "Step 5: Select Money Matters (Input: 1*08100974728*1*2)"
RESPONSE5=$(curl -k -s -X POST $NGROK_URL/ussd \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=${SESSION_ID}&phoneNumber=08100974728&serviceCode=*347*426#&text=1*08100974728*1*2&networkCode=62130")
echo "Response: $RESPONSE5"

# Step 6: Check Balance
echo ""
echo "Step 6: Check Balance (Input: 1*08100974728*1*2*1)"
RESPONSE6=$(curl -k -s -X POST $NGROK_URL/ussd \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=${SESSION_ID}&phoneNumber=08100974728&serviceCode=*347*426#&text=1*08100974728*1*2*1&networkCode=62130")
echo "Response: $RESPONSE6"

# Step 7: Check Both Balances
echo ""
echo "Step 7: Check Both Balances (Input: 1*08100974728*1*2*1*3)"
RESPONSE7=$(curl -k -s -X POST $NGROK_URL/ussd \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=${SESSION_ID}&phoneNumber=08100974728&serviceCode=*347*426#&text=1*08100974728*1*2*1*3&networkCode=62130")
echo "Response: $RESPONSE7"

echo ""
echo "=== TEST COMPLETE ==="
echo "Session ID: $SESSION_ID"
echo "Check application logs for detailed processing information"