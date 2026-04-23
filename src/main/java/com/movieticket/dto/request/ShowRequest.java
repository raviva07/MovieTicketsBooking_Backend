package com.movieticket.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ShowRequest {

    @NotBlank(message = "Movie id is required")
    private String movieId;

    @NotBlank(message = "Theater id is required")
    private String theaterId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Default price must be positive")
    private BigDecimal defaultPrice;

    private Map<String, BigDecimal> seatPricing;

    private String screen;
    private String language;

    private Boolean active = true;
}
