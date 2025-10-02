Got it 👍 — here’s the **entire thing fully in Markdown** so you can copy-paste directly into your `README.md`:

```markdown
# Balanceè USSD Service

A **Spring Boot USSD application** for **Balanceè Tech Solution**, integrated with the **Africa's Talking USSD platform**.

---

## 🚀 Quick Overview
- **Service Code:** `*347*426#`  
- **Framework:** Spring Boot 3.5.5 + Java 21  
- **Integration:** Africa's Talking USSD API  
- **Status:** ✅ Development Complete | ❌ Production Connection Issues  

---

## 📋 Project Structure

AfricanTalkings_USSD/
├── src/main/java/com/harmony2k/africantalkings_ussd/
│ ├── AfricanTalkingsUssdApplication.java # Main application
│ ├── config/
│ │ └── AfricasTalkingConfig.java # Africa's Talking configuration
│ ├── controller/
│ │ └── UssdController.java # USSD endpoints
│ ├── service/
│ │ └── UssdService.java # USSD business logic
│ └── model/
│ ├── Session.java # Session management
│ └── Customer.java # Customer data model
├── src/main/resources/
│ └── application.properties # Configuration
├── scripts/
│ ├── start_application.bat # Windows startup
│ ├── start_ngrok.bat # Ngrok tunnel
│ ├── test_ussd_complete.sh # Complete flow test
│ └── test_live_ussd.sh # Live environment test
└── pom.xml # Maven dependencies



## 🔧 Technical Setup

### Prerequisites
- Java 21  
- Maven 3.6+  
- Ngrok account  
- Africa's Talking live account  

### Configuration
```properties
# application.properties
server.port=8080
africastalking.username=Balancee
africastalking.api.key=your_api_key_here
ngrok.url=https://your-ngrok-url.ngrok-free.app
````

### Dependencies

* Spring Boot 3.5.5
* Africa's Talking Java SDK 3.4.11
* Lombok
* Redis (for session management)

---

## 🎯 USSD Flow

**Complete User Journey:**

1. Dial: `*347*426#`
2. **Welcome:** Accept charges (₦6.98)
3. **Authentication:** Enter phone number
4. **Main Menu:**

   * SOS or repairs
   * Money matters ✅
   * Product purchase
   * Talk to agent

**Money Services:**

* Check balance
* Add money
* Transaction history

---

## 🧪 Sample Customer Data

```java
// Pre-loaded test customers
Customer 1: 08100974728 (Test User) - Wallet: ₦15,250.00
Customer 2: 08031111111 (John Doe) - Wallet: ₦5,000.00
```

---

## 🛠️ API Endpoints

| Endpoint          | Method | Purpose                  |
| ----------------- | ------ | ------------------------ |
| `/ussd`           | POST   | Main USSD callback       |
| `/ussd/health`    | GET    | Service health check     |
| `/ussd/config`    | GET    | Configuration details    |
| `/ussd/test`      | GET    | Basic functionality test |
| `/ussd/simulate`  | POST   | Simulate USSD requests   |
| `/ussd/fast-test` | POST   | Quick connectivity test  |

---

## 🚨 Current Issue: Production Connectivity

### Problem Description

* ✅ Local Development: All USSD flows work perfectly
* ✅ Manual Testing: All endpoints respond correctly (0.8s response time)
* ✅ Ngrok Tunnel: Active and accessible
* ❌ Production USSD: Fails with timeout errors

**Error Messages from Africa's Talking Dashboard**

```
Error: URL Call Failed. Callback failed to respond before timeout: 10 seconds
Status: Failed
Final Response: "END Dear customer, the network is experiencing technical problems..."
```

---

## ✅ Evidence of Working System

### 1. Server Health Check

```bash
curl https://cd7e5ae76b3f.ngrok-free.app/ussd/health
```

**Response:**

```json
{
  "status": "LIVE",
  "service": "Balanceè USSD",
  "callback_url": "https://cd7e5ae76b3f.ngrok-free.app/ussd",
  "service_code": "*347*426#",
  "environment": "LIVE PRODUCTION"
}
```

### 2. USSD Response Time Test

```bash
time curl -X POST "https://cd7e5ae76b3f.ngrok-free.app/ussd" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "sessionId=TEST123&phoneNumber=08100974728&serviceCode=*347*426%23&text=&networkCode=62130"
```

**Result:**

```
real 0m0.803s ✅ (Well under 10-second timeout)
```

### 3. Ngrok Tunnel Status

```bash
curl http://localhost:4040/api/tunnels
```

**Result:** Tunnel active with 21 connections, 41 HTTP requests ✅

---

## 🔍 Root Cause Analysis

The issue is **NOT** with our application code. Evidence shows:

* Our server responds in `0.8s` (well under 10s timeout)
* Ngrok tunnel is stable and accessible
* All manual tests pass perfectly
* Africa's Talking dashboard shows timeout, but our logs show **NO incoming requests**

**Likely causes:**

* Platform-side routing issues
* IP restrictions/whitelisting
* Service activation problems
* Network connectivity between Africa's Talking and our ngrok tunnel

---

## 🛠️ Troubleshooting Steps Taken

* ✅ Verified application starts without errors
* ✅ All API endpoints respond correctly
* ✅ Ngrok tunnel is active and stable
* ✅ Africa's Talking configuration matches requirements
* ✅ API key and username correct
* ✅ USSD business logic works perfectly in testing

---

## 🔄 Testing Commands

```bash
# Complete flow test
./test_ussd_complete.sh

# Live environment test
./test_live_ussd.sh

# Speed test
./test_speed.sh
```

---

## 📞 Africa's Talking Support Required

**Information Provided to Support:**

* Service Code: `*347*426#`
* Callback URL: `https://cd7e5ae76b3f.ngrok-free.app/ussd`
* API Username: `Balancee`
* Company: Balancee Tech Solution

**Requested Investigation:**

* Verify USSD service `*347*426#` is properly activated and routed
* Check for IP restrictions or whitelisting issues
* Confirm callback URL is being called when USSD is dialed
* Identify platform-side configuration problems

---

## 🚀 Getting Started

### Development

```bash
# Start application
./start_application.bat

# Start ngrok tunnel
./start_ngrok.bat

# Run complete tests
./test_ussd_complete.sh
```

### Production Deployment

1. Update `application.properties` with production ngrok URL
2. Verify Africa's Talking dashboard configuration
3. Test USSD code `*347*426#` from a mobile device

---

## 👥 Team Members

* Balancee Tech Solution - Development Team
* Africa's Talking - Platform Provider

---

## 📄 License

This project is proprietary software of **Balancee Tech Solution**.

---

## 🆘 Support Contact

* **Africa's Talking Support**
* Email: [support@africastalking.com](mailto:support@africastalking.com)
* **Issue:** USSD Service `*347*426#` connectivity problem
* **Reference:** Balancee Tech Solution Account

---

**Last Updated:** October 2, 2025
**Status:** Awaiting Africa's Talking platform resolution

