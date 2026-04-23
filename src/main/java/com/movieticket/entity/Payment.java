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

@Document(collection = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed
    @NotNull
    private String bookingId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    private String currency = "INR";

    @NotNull
    private PaymentStatus status;

    /**
     * Razorpay integration fields
     */
    @Indexed
    private String razorpayOrderId;

    @Indexed
    private String razorpayPaymentId;

    private String razorpaySignature;

    /**
     * Payment method (enum for consistency)
     */
    private PaymentMethod method;

    /**
     * Raw gateway response (JSON string)
     */
    private String gatewayResponse;

    /**
     * Actual payment completion time
     */
    private Instant paymentTime;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // ENUM INSIDE ENTITY (as per your design preference)

   
}
