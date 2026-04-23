package com.movieticket.repository;

import com.movieticket.entity.Payment;
import com.movieticket.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    /**
     * Payment for a booking
     */
    Optional<Payment> findByBookingId(String bookingId);

    /**
     * Razorpay references
     */
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    /**
     * Payments by status (admin view)
     */
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    /**
     * Payments in time range (basic reporting)
     */
    List<Payment> findByCreatedAtBetween(Instant from, Instant to);

    /**
     * Payments by status + time range
     */
    List<Payment> findByStatusAndCreatedAtBetween(
            PaymentStatus status,
            Instant from,
            Instant to
    );
}
