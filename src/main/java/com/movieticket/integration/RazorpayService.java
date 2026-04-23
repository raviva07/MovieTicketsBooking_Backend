package com.movieticket.integration;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.movieticket.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayService {

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.keySecret:}")
    private String keySecret;

    public String createOrder(BigDecimal amount, String currency) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentException("Invalid payment amount");
        }

        try {
            long amountInPaise = amount.multiply(BigDecimal.valueOf(100)).longValue();

            JSONObject request = new JSONObject();
            request.put("amount", amountInPaise);
            request.put("currency", currency != null ? currency : "INR");
            request.put("receipt", "txn_" + System.currentTimeMillis());

            Order order = razorpayClient.orders.create(request);

            String orderId = order.get("id");
            log.info("Razorpay order created: {}", orderId);

            return orderId;

        } catch (Exception ex) {
            log.error("Razorpay order creation failed", ex);
            throw new PaymentException("Failed to create order");
        }
    }

    public boolean verifySignature(String orderId,
                                   String paymentId,
                                   String signature) {

        if (orderId == null || paymentId == null || signature == null) {
            return false;
        }

        try {
            String payload = orderId + "|" + paymentId;
            String expected = hmacSha256(payload, keySecret);

            return expected.equals(signature);

        } catch (Exception ex) {
            log.error("Signature verification error", ex);
            return false;
        }
    }

    public JSONObject fetchOrder(String orderId) {
        try {
        	return razorpayClient.orders.fetch(orderId).toJson();

        } catch (Exception ex) {
            throw new PaymentException("Failed to fetch order");
        }
    }

    private String hmacSha256(String data, String secret) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(hash);
    }
}
