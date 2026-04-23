package com.movieticket.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BookingRequest {

    @NotBlank(message = "User id is required")
    private String userId;

    @NotBlank(message = "Show id is required")
    private String showId;

    @NotBlank(message = "Theater id is required")
    private String theaterId;

    @NotEmpty(message = "Seat ids are required")
    private List<@NotBlank String> seatIds;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be positive")
    private BigDecimal totalAmount;

    private String notes;
}

