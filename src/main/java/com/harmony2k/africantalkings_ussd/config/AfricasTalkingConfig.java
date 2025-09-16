package com.harmony2k.africantalkings_ussd.config;

import com.africastalking.AfricasTalking;
import com.africastalking.SmsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
public class AfricasTalkingConfig {

    @Value("${africastalking.username:sandbox}")
    private String username;

    @Value("${africastalking.apikey:atsk_a4da77f6eaf82c8363a6f05104f31e0c82f6f2d79b8cdaba2e4b0b5ea218bcbc29b633a6}")
    private String apiKey;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        try {
            AfricasTalking.initialize(username, apiKey);
            System.out.println("Africa's Talking SDK initialized successfully with username: " + username);
        } catch (Exception e) {
            System.err.println("Failed to initialize Africa's Talking SDK: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Bean
    public SmsService smsService() {
        try {
            return AfricasTalking.getService(SmsService.class);
        } catch (Exception e) {
            System.err.println("Failed to get SMS service: " + e.getMessage());
            return null;
        }
    }
}