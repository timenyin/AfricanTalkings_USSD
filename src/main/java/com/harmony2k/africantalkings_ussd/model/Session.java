package com.harmony2k.africantalkings_ussd.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Session {
    private String sessionId;
    private String phoneNumber;
    private String serviceCode;
    private String text;
    private String networkCode;
    private int currentMenuLevel;
    private String customerName;
    private String customerPhone;
    private double walletBalance;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessed;

    public Session(String sessionId, String phoneNumber, String serviceCode, String text, String networkCode) {
        this.sessionId = sessionId;
        this.phoneNumber = phoneNumber;
        this.serviceCode = serviceCode;
        this.text = text;
        this.networkCode = networkCode;
        this.currentMenuLevel = 0;
        this.walletBalance = 0.0;
        this.createdAt = LocalDateTime.now();
        this.lastAccessed = LocalDateTime.now();
    }

    public String[] getInputArray() {
        if (text == null || text.isEmpty()) {
            return new String[0];
        }
        return text.split("\\*");
    }

    public String getLastInput() {
        String[] inputs = getInputArray();
        if (inputs.length > 0) {
            return inputs[inputs.length - 1];
        }
        return "";
    }

    public void updateAccess() {
        this.lastAccessed = LocalDateTime.now();
    }
}