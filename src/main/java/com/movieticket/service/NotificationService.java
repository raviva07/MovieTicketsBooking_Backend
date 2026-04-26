package com.movieticket.service;

import com.movieticket.dto.request.NotificationRequest;
import com.movieticket.dto.response.NotificationResponse;
import com.movieticket.entity.Notification;
import com.movieticket.entity.NotificationType;
import com.movieticket.entity.User;
import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.integration.EmailService;
import com.movieticket.mapper.NotificationMapper;
import com.movieticket.repository.NotificationRepository;
import com.movieticket.repository.UserRepository;

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
    private final UserRepository userRepository;

    // ================= SEND =================
    public NotificationResponse send(NotificationRequest req) {

        if (req.getType() == null) {
            req.setType(NotificationType.GENERAL);
        }

        // 🔥 FIX 1: validate user first
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 🔥 FIX 2: ALWAYS save notification FIRST (important)
        Notification entity = notificationMapper.toEntity(req);
        entity.setUserId(user.getId()); // ensure correct ID

        Notification saved = notificationRepository.save(entity);

        log.info("📩 Notification saved in DB for userId: {}", user.getId());

        // 🔥 Email optional
        String email = user.getEmail();

        if (isRealEmail(email)) {
            try {
                emailService.sendEmail(email, req.getTitle(), req.getMessage());
                log.info("📧 Email sent to {}", email);
            } catch (Exception e) {
                log.error("Email failed but notification stored", e);
            }
        } else {
            log.info("Fake email detected. Skipping email.");
        }

        return notificationMapper.toResponse(saved);
    }

    // ================= GET BY USER =================
    public List<NotificationResponse> getByUser(String userId) {

        // 🔥 FIX 3: verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> list = notificationRepository.findByUserId(userId);

        log.info("🔔 Notifications fetched: {}", list.size());

        return list.stream()
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

    // ================= EMAIL CHECK =================
    private boolean isRealEmail(String email) {
        return email != null
                && email.contains("@")
                && email.contains(".");
    }
}
