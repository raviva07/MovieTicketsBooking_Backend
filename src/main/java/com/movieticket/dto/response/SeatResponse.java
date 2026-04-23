package com.movieticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SeatResponse {

    private String id;

    private String theaterId;

    private String seatNumber;
    private String row;
    private Integer position;

    private String type;

    private BigDecimal basePrice;

    /**
     * SeatStatus enum → String
     */
    private String status;

    /**
     * Changed to structured metadata
     */
    private String metadata;
}
