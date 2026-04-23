package com.movieticket.service;

import com.movieticket.dto.request.PaymentRequest;
import com.movieticket.dto.request.PaymentVerifyRequest;
import com.movieticket.dto.response.PaymentResponse;
import com.movieticket.entity.*;
import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.repository.BookingRepository;
import com.movieticket.repository.PaymentRepository;
import com.movieticket.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Value("${razorpay.keyId}")
    private String razorpayKeyId;

    @Value("${razorpay.keySecret}")
    private String razorpayKeySecret;

    // ================= INITIATE =================
    public PaymentResponse initiatePayment(PaymentRequest req, String username) {

        Booking booking = bookingRepository.findById(req.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // ✅ SECURITY FIX (IMPORTANT)
        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!booking.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized booking access");
        }

        try {
        	RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);


            JSONObject options = new JSONObject();
            options.put(
                    "amount",
                    booking.getTotalAmount()
                            .multiply(BigDecimal.valueOf(100))
                            .intValue()
            );
            options.put("currency", booking.getCurrency());
            options.put("receipt", booking.getId());

            Order order = client.orders.create(options);

            Payment payment = Payment.builder()
                    .bookingId(booking.getId())
                    .amount(booking.getTotalAmount())
                    .currency(booking.getCurrency())
                    .status(PaymentStatus.INITIATED)
                    .razorpayOrderId(order.get("id"))
                    .method(PaymentMethod.valueOf(req.getMethod().toUpperCase()))
                    .createdAt(Instant.now())
                    .build();

            paymentRepository.save(payment);

            return mapToResponse(payment);

        } catch (Exception e) {
            log.error("❌ Payment initiation failed", e);
            throw new RuntimeException("Payment initiation failed");
        }
    }

    // ================= VERIFY =================
    public PaymentResponse verifyPayment(PaymentVerifyRequest req, String username) {

        Payment payment = paymentRepository
                .findByRazorpayOrderId(req.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        Booking booking = bookingRepository.findById(payment.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!booking.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized payment verification");
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setRazorpayPaymentId(req.getRazorpayPaymentId());
        payment.setRazorpaySignature(req.getRazorpaySignature());
        payment.setPaymentTime(Instant.now());

        paymentRepository.save(payment);

        return mapToResponse(payment);
    }


    // ================= GET BY BOOKING =================
    public PaymentResponse getByBookingId(String bookingId, String username) {

        Optional<Payment> optional = paymentRepository.findByBookingId(bookingId);

        if (optional.isEmpty()) return null;

        Payment payment = optional.get();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // ✅ SECURITY CHECK
        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!booking.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        return mapToResponse(payment);
    }

    // ================= MAPPER =================
    private PaymentResponse mapToResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .bookingId(p.getBookingId())
                .amount(p.getAmount())
                .status(p.getStatus().name())
                .razorpayOrderId(p.getRazorpayOrderId())
                .razorpayPaymentId(p.getRazorpayPaymentId())
                .build();
    }
}
