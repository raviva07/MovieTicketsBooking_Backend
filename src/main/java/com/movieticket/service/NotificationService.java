package com.movieticket.service;

import com.movieticket.dto.request.NotificationRequest;
import com.movieticket.dto.response.NotificationResponse;
import com.movieticket.entity.Notification;
import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.integration.EmailService;
import com.movieticket.mapper.NotificationMapper;
import com.movieticket.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailService emailService;

    // ================= SEND =================
    public NotificationResponse send(NotificationRequest req) {

        Notification entity = notificationMapper.toEntity(req);
        Notification saved = notificationRepository.save(entity);

        // Fire email notification
        emailService.sendEmail(req.getUserId(), req.getTitle(), req.getMessage());

        return notificationMapper.toResponse(saved);
    }

    // ================= GET BY USER =================
    public List<NotificationResponse> getByUser(String userId) {
        return notificationRepository.findByUserId(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ================= MARK AS READ =================
    public void markAsRead(String id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }
}
