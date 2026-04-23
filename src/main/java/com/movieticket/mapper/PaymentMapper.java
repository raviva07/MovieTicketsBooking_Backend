package com.movieticket.mapper;

import com.movieticket.dto.request.PaymentRequest;
import com.movieticket.dto.response.PaymentResponse;
import com.movieticket.entity.Payment;
import com.movieticket.entity.PaymentMethod;
import com.movieticket.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    /**
     * Convert Entity → Response DTO
     */
    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) return null;

        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus() != null ? payment.getStatus().name() : null)
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                // ✅ FIXED: Enum → String
                .method(payment.getMethod() != null ? payment.getMethod().name() : null)
                .gatewayResponse(payment.getGatewayResponse())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    /**
     * Convert Request DTO → Entity
     */
    public Payment toEntity(PaymentRequest req) {
        if (req == null) return null;

        return Payment.builder()
                .bookingId(req.getBookingId())
                .amount(req.getAmount())
                .currency("INR")
                // ✅ String → Enum (SAFE)
                .method(parseMethod(req.getMethod()))
                .razorpayOrderId(req.getRazorpayOrderId())
                .razorpayPaymentId(req.getRazorpayPaymentId())
                .razorpaySignature(req.getRazorpaySignature())
                .build();
    }

    /**
     * Update existing entity
     */
    public void updateEntity(Payment payment, PaymentRequest req) {
        if (payment == null || req == null) return;

        if (req.getAmount() != null) {
            payment.setAmount(req.getAmount());
        }

        if (req.getMethod() != null) {
            payment.setMethod(parseMethod(req.getMethod()));
        }

        if (req.getRazorpayOrderId() != null) {
            payment.setRazorpayOrderId(req.getRazorpayOrderId());
        }

        if (req.getRazorpayPaymentId() != null) {
            payment.setRazorpayPaymentId(req.getRazorpayPaymentId());
        }

        if (req.getRazorpaySignature() != null) {
            payment.setRazorpaySignature(req.getRazorpaySignature());
        }
    }

    /**
     * 🔐 Safe conversion: String → Enum
     */
    private PaymentMethod parseMethod(String method) {
        if (method == null || method.isBlank()) return null;

        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid payment method: " + method);
        }
    }
}

