package com.harmony2k.africantalkings_ussd.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BalanceService {
    private final Map<String, BigDecimal> walletBalances = new ConcurrentHashMap<>();
    private final Map<String, BigDecimal> creditBalances = new ConcurrentHashMap<>();

    public BalanceService() {
        // Initialize with demo data
        walletBalances.put("+254700000000", new BigDecimal("15000.00"));
        walletBalances.put("+254711000000", new BigDecimal("7500.50"));

        creditBalances.put("+254700000000", new BigDecimal("5000.00"));
        creditBalances.put("+254711000000", new BigDecimal("2500.75"));
    }

    public BigDecimal getWalletBalance(String phoneNumber) {
        return walletBalances.getOrDefault(phoneNumber, BigDecimal.ZERO);
    }

    public BigDecimal getCreditBalance(String phoneNumber) {
        return creditBalances.getOrDefault(phoneNumber, BigDecimal.ZERO);
    }

    public boolean deductWallet(String phoneNumber, BigDecimal amount) {
        BigDecimal currentBalance = getWalletBalance(phoneNumber);
        if (currentBalance.compareTo(amount) >= 0) {
            walletBalances.put(phoneNumber, currentBalance.subtract(amount));
            return true;
        }
        return false;
    }

    public boolean deductCredit(String phoneNumber, BigDecimal amount) {
        BigDecimal currentBalance = getCreditBalance(phoneNumber);
        if (currentBalance.compareTo(amount) >= 0) {
            creditBalances.put(phoneNumber, currentBalance.subtract(amount));
            return true;
        }
        return false;
    }

    public void addToWallet(String phoneNumber, BigDecimal amount) {
        BigDecimal currentBalance = getWalletBalance(phoneNumber);
        walletBalances.put(phoneNumber, currentBalance.add(amount));
    }

    public void addToCredit(String phoneNumber, BigDecimal amount) {
        BigDecimal currentBalance = getCreditBalance(phoneNumber);
        creditBalances.put(phoneNumber, currentBalance.add(amount));
    }
}