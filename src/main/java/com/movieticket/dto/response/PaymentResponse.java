package com.movieticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String id;
    private String bookingId;

    private BigDecimal amount;
    private String currency;

    /**
     * Enum converted to String in mapper
     */
    private String status;

    private String razorpayOrderId;
    private String razorpayPaymentId;

    private String method;

    private String gatewayResponse;

    private Instant createdAt;
    private String razorpayKey;
}
