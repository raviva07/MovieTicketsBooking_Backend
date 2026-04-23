package com.movieticket.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class NotificationResponse {

    private String id;
    private String userId;

    private String title;
    private String message;

    private boolean read;

    private String referenceId;

    private Instant createdAt;
}
