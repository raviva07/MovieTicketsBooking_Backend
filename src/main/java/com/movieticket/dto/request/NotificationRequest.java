package com.movieticket.dto.request;

import com.movieticket.entity.NotificationType;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRequest {

    @NotBlank(message = "User id is required")
    private String userId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    private String referenceId;
    @NotBlank(message = "Email is required")
    private String email;
    private NotificationType type;

}

