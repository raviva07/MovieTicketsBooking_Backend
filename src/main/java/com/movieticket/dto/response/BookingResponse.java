package com.movieticket.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class BookingResponse {

    private String id;

    private String userId;
    private String showId;
    private String theaterId;

    private List<String> seatIds;

    // NEW: friendly seat numbers (A1, B2, ...)
    private List<String> seatNumbers;

    private BigDecimal totalAmount;
    private String currency;

    private String paymentId;

    /**
     * BookingStatus enum → String
     */
    private String status;

    private Instant createdAt;
    private Instant expiresAt;

    private String notes;
}
