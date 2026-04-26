package com.movieticket.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;

@Document(collection = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed
    @NotNull
    private String userId;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    private boolean read = false;

    /**
     * Type of notification (important for frontend handling)
     */
    @NotNull
    private NotificationType type;

    /**
     * Optional reference (bookingId, paymentId, showId)
     */
    private String referenceId;

    private ReferenceType referenceType;

   
    
    

    
}
