package com.movieticket.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "shows")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Show implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed
    @NotNull
    private String movieId;

    @Indexed
    @NotNull
    private String theaterId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    /**
     * seatId -> SeatStatus (per-show availability)
     */
    private Map<String, SeatStatus> seatStatusMap = new HashMap<>();

    /**
     * seatId -> price override
     */
    private Map<String, BigDecimal> seatPricing = new HashMap<>();

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal defaultPrice;

    private Integer availableSeats;

    private String screen;
    private String language;

    private boolean active = true;

    private Map<String, String> metadata = new HashMap<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
