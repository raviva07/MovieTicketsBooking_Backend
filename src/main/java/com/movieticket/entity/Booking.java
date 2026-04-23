package com.movieticket.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(collection = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed
    @NotNull
    private String userId;

    @Indexed
    @NotNull
    private String showId;

    @NotNull
    private String theaterId;

    @NotEmpty
    private List<@NotBlank String> seatIds;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal totalAmount;

    private String currency = "INR";

    /**
     * Internal or external payment reference
     */
    private String paymentId;

    @NotNull
    private BookingStatus status;

    @NotNull
    private PaymentStatus paymentStatus;

    /**
     * Business booking time
     */
    private Instant bookingTime;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Seat lock expiry (important for scheduler)
     */
    private Instant expiresAt;

    private String notes;
}
