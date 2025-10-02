package com.harmony2k.africantalkings_ussd.model;

import lombok.Data;

@Data
public class Customer {
    private String phoneNumber;
    private String fullName;
    private String customerId;
    private double walletBalance;
    private double creditBalance;
    private boolean isActive;

    public Customer(String phoneNumber, String fullName) {
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.customerId = "BAL" + System.currentTimeMillis();
        this.walletBalance = 0.0;
        this.creditBalance = 0.0;
        this.isActive = true;
    }
}