package com.movieticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticket.config.JwtFilter;
import com.movieticket.config.TestSecurityConfig;
import com.movieticket.dto.request.NotificationRequest;
import com.movieticket.dto.response.NotificationResponse;
import com.movieticket.service.NotificationService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtFilter jwtFilter;

    // ================= SEND =================
    @Test
    void sendNotification_shouldReturnResponse() throws Exception {

        NotificationRequest req = new NotificationRequest();
        req.setUserId("user1");
        req.setTitle("Test Title");
        req.setMessage("Test Message");

        NotificationResponse resp = NotificationResponse.builder()
                .id("notif1")
                .userId("user1")
                .title("Test Title")
                .message("Test Message")
                .read(false)
                .createdAt(Instant.now())
                .build();

        when(notificationService.send(any())).thenReturn(resp);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notification sent successfully"))
                .andExpect(jsonPath("$.data.id").value("notif1"));

        verify(notificationService, times(1)).send(any());
    }

    // ================= MY NOTIFICATIONS =================
    @Test
    void getMyNotifications_shouldReturnList() throws Exception {

        NotificationResponse resp = NotificationResponse.builder()
                .id("notif1")
                .userId("user1")
                .title("Test Title")
                .message("Test Message")
                .read(false)
                .createdAt(Instant.now())
                .build();

        when(notificationService.getByUser("user1"))
                .thenReturn(List.of(resp));

        // 🔥 FIX SECURITY CONTEXT (THIS IS THE KEY FIX)
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user1", null, List.of())
        );

        mockMvc.perform(get("/api/notifications/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notifications fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value("notif1"));

        verify(notificationService, times(1)).getByUser("user1");
    }

    // ================= MARK AS READ =================
    @Test
    void markAsRead_shouldReturnResponse() throws Exception {

        doNothing().when(notificationService).markAsRead("notif1");

        mockMvc.perform(put("/api/notifications/notif1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notification marked as read"))
                .andExpect(jsonPath("$.data").value("SUCCESS"));

        verify(notificationService, times(1)).markAsRead("notif1");
    }
}
