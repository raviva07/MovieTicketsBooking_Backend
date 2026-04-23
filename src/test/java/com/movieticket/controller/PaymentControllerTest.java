package com.movieticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticket.config.JwtFilter;
import com.movieticket.config.TestSecurityConfig;
import com.movieticket.dto.request.PaymentRequest;
import com.movieticket.dto.request.PaymentVerifyRequest;
import com.movieticket.dto.response.PaymentResponse;
import com.movieticket.entity.PaymentStatus;
import com.movieticket.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ INITIATE (FIXED)
    @Test
    void initiatePayment_Success() throws Exception {

        PaymentRequest req = new PaymentRequest();
        req.setBookingId("b1");
        req.setMethod("CARD");
        req.setAmount(BigDecimal.valueOf(100)); // ✅ FIX: REQUIRED FIELD

        PaymentResponse res = PaymentResponse.builder()
                .id("p1")
                .bookingId("b1")
                .amount(BigDecimal.valueOf(100))
                .status(PaymentStatus.INITIATED.name())
                .build();

        Mockito.when(paymentService.initiatePayment(any(), anyString()))
                .thenReturn(res);

        mockMvc.perform(post("/api/payments/initiate")
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("p1"));
    }


    // ✅ VERIFY (FIXED)
    @Test
    void verifyPayment_Success() throws Exception {

        PaymentVerifyRequest req = new PaymentVerifyRequest();
        req.setBookingId("b1");
        req.setRazorpayOrderId("order1");
        req.setRazorpayPaymentId("pay1");
        req.setRazorpaySignature("sig1");

        PaymentResponse res = PaymentResponse.builder()
                .id("p1")
                .bookingId("b1")
                .status(PaymentStatus.SUCCESS.name())
                .build();

        Mockito.when(paymentService.verifyPayment(any(), anyString()))
                .thenReturn(res);

        mockMvc.perform(post("/api/payments/verify")
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("p1"));
    }

    // ✅ GET SUCCESS (FIXED)
    @Test
    void getPaymentByBookingId_Success() throws Exception {

        PaymentResponse res = PaymentResponse.builder()
                .id("p1")
                .bookingId("booking1")
                .status(PaymentStatus.SUCCESS.name())
                .build();

        Mockito.when(paymentService.getByBookingId(anyString(), anyString()))
                .thenReturn(res);

        mockMvc.perform(get("/api/payments/booking/booking1")
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("p1"));
    }

    // ✅ GET NULL (FIXED — NO 500)
    @Test
    void getPaymentByBookingId_NotFound() throws Exception {

        Mockito.when(paymentService.getByBookingId(anyString(), anyString()))
                .thenReturn(null);

        mockMvc.perform(get("/api/payments/booking/booking1")
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}


