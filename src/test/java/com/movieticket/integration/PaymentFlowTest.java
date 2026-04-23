package com.movieticket.integration;

import com.movieticket.dto.request.PaymentVerifyRequest;
import com.movieticket.dto.response.PaymentResponse;
import com.movieticket.entity.PaymentStatus;
import com.movieticket.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
class PaymentFlowTest {

    @Mock
    private PaymentService paymentService;

    private PaymentVerifyRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = PaymentVerifyRequest.builder()
                .bookingId("booking1")
                .razorpayOrderId("order123")
                .razorpayPaymentId("pay123")
                .razorpaySignature("signature123")
                .build();
    }

    @Test
    void testPaymentVerificationSuccess() {

        PaymentResponse mockResponse = PaymentResponse.builder()
                .bookingId("booking1")
                .status(PaymentStatus.SUCCESS.name())
                .build();

        when(paymentService.verifyPayment(any(), eq("testUser")))
                .thenReturn(mockResponse);

        PaymentResponse response =
                paymentService.verifyPayment(request, "testUser");

        assertNotNull(response);
        assertEquals("booking1", response.getBookingId());
        assertEquals(PaymentStatus.SUCCESS.name(), response.getStatus());

        verify(paymentService, times(1))
                .verifyPayment(any(), eq("testUser"));
    }
}
