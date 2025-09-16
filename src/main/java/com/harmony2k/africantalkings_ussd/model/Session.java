package com.harmony2k.africantalkings_ussd.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Session {
    private String sessionId;
    private String phoneNumber;
    private Instant lastUpdated;
    private Map<String, String> data = new HashMap<>();

    public Session(String sessionId, String phoneNumber) {
        this.sessionId = sessionId;
        this.phoneNumber = phoneNumber;
        this.lastUpdated = Instant.now();
    }

    public void touch() {
        this.lastUpdated = Instant.now();
    }

    public boolean isExpired(long timeoutMs) {
        return Instant.now().toEpochMilli() - lastUpdated.toEpochMilli() > timeoutMs;
    }

    // Convenience methods for data access
    public void setData(String key, String value) {
        this.data.put(key, value);
    }

    public String getData(String key) {
        return this.data.get(key);
    }

    // Getters and setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}