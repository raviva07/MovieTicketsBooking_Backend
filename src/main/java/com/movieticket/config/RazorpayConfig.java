package com.movieticket.config;

import com.razorpay.RazorpayClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Value("${razorpay.keyId}")
    private String keyId;

    @Value("${razorpay.keySecret}")
    private String keySecret;

    @Bean
    public RazorpayClient razorpayClient() throws Exception {
        return new RazorpayClient(keyId, keySecret);
    }

    @PostConstruct
    public void validate() {
        if (keyId == null || keyId.isBlank() ||
            keySecret == null || keySecret.isBlank()) {
            throw new IllegalStateException("Razorpay keys not configured");
        }
    }
}
