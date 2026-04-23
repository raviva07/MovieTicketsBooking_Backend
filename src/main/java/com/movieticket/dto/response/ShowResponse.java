package com.movieticket.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ShowResponse {

    private String id;

    private String movieId;
    private String theaterId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /**
     * Seat status map converted to String values
     */
    private Map<String, String> seatStatusMap;

    private Map<String, BigDecimal> seatPricing;

    private BigDecimal defaultPrice;

    private Integer availableSeats;

    private String screen;
    private String language;

    private Boolean active;

    /**
     * Cleaned metadata type
     */
    private Map<String, String> metadata;
}
