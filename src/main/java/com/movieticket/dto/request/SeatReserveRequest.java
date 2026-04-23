package com.movieticket.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class SeatReserveRequest {

    @NotBlank(message = "Show id is required")
    private String showId;

    @NotBlank(message = "User id is required")
    private String userId;

    @NotEmpty(message = "At least one seat must be selected")
    private List<@NotBlank String> seatIds;

    @Min(value = 30, message = "Minimum lock time is 30 seconds")
    @Max(value = 600, message = "Maximum lock time is 10 minutes")
    private Long lockSeconds;
}
