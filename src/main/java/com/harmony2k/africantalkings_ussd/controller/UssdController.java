package com.harmony2k.africantalkings_ussd.controller;

import com.harmony2k.africantalkings_ussd.config.AfricasTalkingConfig;
import com.harmony2k.africantalkings_ussd.service.UssdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/ussd")
public class UssdController {

    @Autowired
    private UssdService ussdService;

    @Autowired
    private AfricasTalkingConfig africastalkingConfig;

    private AtomicLong requestCounter = new AtomicLong(0);

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String handleUssdRequest(HttpServletRequest request, @RequestParam Map<String, String> params) {

        long requestId = requestCounter.incrementAndGet();

        // EXTENSIVE LOGGING
        System.out.println("🚨 ======= AFRICA'S TALKING REQUEST DEBUG =======");
        System.out.println("🚨 TIMESTAMP: " + java.time.LocalDateTime.now());
        System.out.println("🚨 REQUEST ID: " + requestId);

        // Log all headers
        System.out.println("🚨 HEADERS:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println("🚨   " + headerName + ": " + headerValue);
        }

        // Log all parameters
        System.out.println("🚨 PARAMETERS:");
        params.forEach((key, value) -> System.out.println("🚨   " + key + ": " + value));

        // Log remote information
        System.out.println("🚨 REMOTE INFO:");
        System.out.println("🚨   Remote Addr: " + request.getRemoteAddr());
        System.out.println("🚨   Remote Host: " + request.getRemoteHost());
        System.out.println("🚨   Remote Port: " + request.getRemotePort());
        System.out.println("🚨   Server Name: " + request.getServerName());
        System.out.println("🚨   Server Port: " + request.getServerPort());

        System.out.println("🚨 =============================================");

        String sessionId = params.get("sessionId");
        String phoneNumber = params.get("phoneNumber");
        String serviceCode = params.get("serviceCode");
        String text = params.get("text");
        String networkCode = params.get("networkCode");

        // Handle case where parameters might have different names
        if (sessionId == null) sessionId = params.get("sessionID");
        if (phoneNumber == null) phoneNumber = params.get("phone");
        if (serviceCode == null) serviceCode = params.get("serviceCode");
        if (text == null) text = params.get("text");
        if (networkCode == null) networkCode = params.get("networkCode");

        try {
            // If still no sessionId or phoneNumber, return immediate response
            if (sessionId == null || sessionId.trim().isEmpty() || phoneNumber == null || phoneNumber.trim().isEmpty()) {
                System.out.println("🚨 RETURNING WELCOME SCREEN (missing params)");
                return "CON Welcome to Balanceè\n\n1. Start Service\n0. Exit";
            }

            long startTime = System.currentTimeMillis();
            String response = ussdService.processUssdRequest(sessionId, phoneNumber, serviceCode, text, networkCode);
            long processingTime = System.currentTimeMillis() - startTime;

            System.out.println("✅ RESPONSE SENT IN " + processingTime + "ms: " + response);

            if (response == null) {
                response = "CON Welcome to Balanceè\n\n1. Start Service\n0. Exit";
            }

            return response;

        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            return "CON Welcome to Balanceè\n\n1. Start Service\n0. Exit";
        }
    }

    // Simple endpoint for Africa's Talking connectivity test
    @PostMapping(value = "/at-test", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String africaTalkingTest(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("phoneNumber") String phoneNumber) {
        System.out.println("✅ Africa's Talking Test Received - Session: " + sessionId);
        return "CON Africa's Talking Connection Test - SUCCESS\n\n1. Continue\n0. Exit";
    }

    // Even simpler endpoint for basic connectivity test
    @GetMapping("/connectivity-test")
    public String connectivityTest() {
        System.out.println("✅ Connectivity test received at: " + java.time.LocalDateTime.now());
        return "CONNECTED_" + System.currentTimeMillis();
    }

    // Fast test endpoint for Africa's Talking
    @PostMapping(value = "/fast-test", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String fastTest(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("phoneNumber") String phoneNumber) {
        return ussdService.quickTest(sessionId, phoneNumber);
    }

    @PostMapping(value = "/events", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String handleUssdEvents(@RequestParam Map<String, String> params) {
        System.out.println("📊 USSD Event received at: " + java.time.LocalDateTime.now());
        return "Accepted";
    }

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "LIVE");
        health.put("service", "Balanceè USSD");
        health.put("callback_url", africastalkingConfig.getCallbackUrl());
        health.put("service_code", "*347*426#");
        health.put("request_count", requestCounter.get());
        health.put("timestamp", java.time.Instant.now().toString());
        health.put("environment", "LIVE PRODUCTION");
        health.put("response_time", "OPTIMIZED");
        return health;
    }

    @GetMapping("/ping")
    public String ping() {
        return "ALIVE_" + System.currentTimeMillis();
    }

    @PostMapping(value = "/simulate", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String, Object>> simulateUssdRequest(
            @RequestParam String phoneNumber,
            @RequestParam String text) {

        String sessionId = "SIM_" + System.currentTimeMillis();

        Map<String, Object> result = new HashMap<>();
        result.put("session_id", sessionId);
        result.put("phone_number", phoneNumber);
        result.put("input_text", text);
        result.put("timestamp", java.time.LocalDateTime.now().toString());

        try {
            String response = ussdService.processUssdRequest(
                    sessionId, phoneNumber, "*347*426#", text, "62130");
            result.put("response", response);
            result.put("status", "success");
        } catch (Exception e) {
            result.put("response", "END Simulation error: " + e.getMessage());
            result.put("status", "error");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/config")
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("current_ngrok_url", africastalkingConfig.getNgrokUrl());
        config.put("callback_url", africastalkingConfig.getCallbackUrl());
        config.put("events_url", africastalkingConfig.getEventsUrl());
        config.put("username", africastalkingConfig.getUsername());
        config.put("api_key_set", africastalkingConfig.getApiKey() != null && !africastalkingConfig.getApiKey().isEmpty() ? "YES" : "NO");
        config.put("note", "Update ngrok.url in application.properties when ngrok restarts");
        return config;
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Balanceè USSD Service is running!");
        response.put("status", "OK");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        response.put("request_count", requestCounter.get());
        response.put("callback_url", africastalkingConfig.getCallbackUrl());

        Map<String, Object> testCustomer = new HashMap<>();
        testCustomer.put("phone", "08100974728");
        testCustomer.put("name", "Test User");
        testCustomer.put("wallet_balance", 15250.0);
        testCustomer.put("credit_balance", 7500.0);
        response.put("test_customer", testCustomer);

        return ResponseEntity.ok(response);
    }
}