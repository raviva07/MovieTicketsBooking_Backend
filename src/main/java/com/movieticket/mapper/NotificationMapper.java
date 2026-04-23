package com.movieticket.mapper;

import com.movieticket.dto.request.NotificationRequest;
import com.movieticket.dto.response.NotificationResponse;
import com.movieticket.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) return null;

        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.isRead())
                .referenceId(notification.getReferenceId())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public Notification toEntity(NotificationRequest req) {
        if (req == null) return null;

        return Notification.builder()
                .userId(req.getUserId())
                .title(req.getTitle())
                .message(req.getMessage())
                .referenceId(req.getReferenceId())
                .build();
    }
}
