package com.movieticket.service;

import com.movieticket.dto.request.PaymentVerifyRequest;
import com.movieticket.dto.response.PaymentResponse;
import com.movieticket.entity.*;
import com.movieticket.repository.BookingRepository;
import com.movieticket.repository.PaymentRepository;
import com.movieticket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Booking booking;
    private User user;
    private Payment payment;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId("u1");
        user.setEmail("testUser");

        booking = new Booking();
        booking.setId("b1");
        booking.setUserId("u1");

        payment = Payment.builder()
                .id("p1")
                .bookingId("b1")
                .amount(BigDecimal.valueOf(100))
                .status(PaymentStatus.SUCCESS) // ✅ IMPORTANT FIX
                .build();
    }

    // ✅ VERIFY PAYMENT
    @Test
    void verifyPayment_Success() {

        PaymentVerifyRequest req = new PaymentVerifyRequest();
        req.setRazorpayOrderId("order1");
        req.setRazorpayPaymentId("pay1");
        req.setRazorpaySignature("sig1");

        when(paymentRepository.findByRazorpayOrderId("order1"))
                .thenReturn(Optional.of(payment));

        when(bookingRepository.findById("b1"))
                .thenReturn(Optional.of(booking));

        when(userRepository.findByEmail("testUser"))
                .thenReturn(Optional.of(user));

        PaymentResponse res = paymentService.verifyPayment(req, "testUser");

        assertNotNull(res);
        assertEquals("b1", res.getBookingId());
        assertEquals(PaymentStatus.SUCCESS.name(), res.getStatus());
    }

    // ✅ GET PAYMENT
    @Test
    void getByBookingId_Success() {

        when(paymentRepository.findByBookingId("b1"))
                .thenReturn(Optional.of(payment));

        when(bookingRepository.findById("b1"))
                .thenReturn(Optional.of(booking));

        when(userRepository.findByEmail("testUser"))
                .thenReturn(Optional.of(user));

        PaymentResponse res = paymentService.getByBookingId("b1", "testUser");

        assertNotNull(res);
        assertEquals("b1", res.getBookingId());
        assertEquals(PaymentStatus.SUCCESS.name(), res.getStatus());
    }

    // ✅ NULL CASE
    @Test
    void getByBookingId_NotFound() {

        when(paymentRepository.findByBookingId("b1"))
                .thenReturn(Optional.empty());

        PaymentResponse res = paymentService.getByBookingId("b1", "testUser");

        assertNull(res);
    }
}
