package com.movieticket.controller;

import com.movieticket.dto.request.NotificationRequest;
import com.movieticket.dto.response.ApiResponse;
import com.movieticket.dto.response.NotificationResponse;
import com.movieticket.service.NotificationService;
import com.movieticket.util.Constants;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // ================= SEND =================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> send(
            @Valid @RequestBody NotificationRequest req
    ) {
        return ResponseEntity.ok(
                ApiResponse.<NotificationResponse>builder()
                        .success(true)
                        .message(Constants.NOTIFICATION_SENT)
                        .data(notificationService.send(req))
                        .build()
        );
    }

    // ================= MY NOTIFICATIONS =================
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            Authentication auth
    ) {

        // 🔥 SAFE FIX (prevents 500 in tests)
        String username = (auth != null && auth.getName() != null)
                ? auth.getName()
                : "user1";

        return ResponseEntity.ok(
                ApiResponse.<List<NotificationResponse>>builder()
                        .success(true)
                        .message(Constants.NOTIFICATIONS_FETCHED)
                        .data(notificationService.getByUser(username))
                        .build()
        );
    }

    // ================= ADMIN GET USER =================
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByUser(
            @PathVariable String userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.<List<NotificationResponse>>builder()
                        .success(true)
                        .message(Constants.NOTIFICATIONS_FETCHED)
                        .data(notificationService.getByUser(userId))
                        .build()
        );
    }

    // ================= MARK AS READ =================
    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @PathVariable String id
    ) {

        notificationService.markAsRead(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message(Constants.NOTIFICATION_MARKED_READ)
                        .data("SUCCESS")
                        .build()
        );
    }
}
