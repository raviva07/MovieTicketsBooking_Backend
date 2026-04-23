package com.movieticket.repository;

import com.movieticket.entity.Payment;
import com.movieticket.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@DataMongoTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        payment = new Payment();
        payment.setBookingId("b1");
        payment.setStatus(PaymentStatus.INITIATED);
        payment = paymentRepository.save(payment);
    }

    @Test
    void testFindById() {
        Optional<Payment> found = paymentRepository.findById(payment.getId());
        assertTrue(found.isPresent());
        assertEquals("b1", found.get().getBookingId());
    }

    @Test
    void testFindByBookingId() {
        Optional<Payment> found = paymentRepository.findByBookingId("b1");
        assertTrue(found.isPresent());
    }

    @Test
    void testDelete() {
        paymentRepository.delete(payment);
        assertFalse(paymentRepository.findById(payment.getId()).isPresent());
    }
}

