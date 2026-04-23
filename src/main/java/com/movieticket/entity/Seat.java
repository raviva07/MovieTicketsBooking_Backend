package com.movieticket.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "seats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "theater_seat_idx", def = "{'theaterId': 1, 'seatNumber': 1}", unique = true)
public class Seat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    private String theaterId;

    /**
     * A1, B12
     */
    @NotBlank
    private String seatNumber;

    private String row;
    private String showId;

    private Integer position;

    @NotNull
    private SeatType type;

    @NotNull
    private BigDecimal basePrice;

    private SeatStatus status = SeatStatus.AVAILABLE;

    private String metadata;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private String lockedBy;
    private Instant lockedAt;


    
}
