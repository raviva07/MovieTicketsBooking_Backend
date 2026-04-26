package com.movieticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticket.config.JwtFilter;
import com.movieticket.config.TestSecurityConfig;
import com.movieticket.dto.request.NotificationRequest;
import com.movieticket.dto.response.NotificationResponse;
import com.movieticket.entity.User;
import com.movieticket.repository.UserRepository;

import com.movieticket.service.NotificationService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false) // ✅ IMPORTANT
@Import(TestSecurityConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtFilter jwtFilter;

    // ================= SEND =================
    @Test
    void sendNotification_shouldReturnResponse() throws Exception {

        NotificationRequest req = new NotificationRequest();
        req.setUserId("user1");
        req.setTitle("Test Title");
        req.setMessage("Test Message");
        req.setEmail("user@gmail.com");

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
                .andExpect(jsonPath("$.data.id").value("notif1"));
    }

    // ================= MY NOTIFICATIONS =================
    @Test
    void getMyNotifications_shouldReturnList() throws Exception {

        // ⚠️ MUST match your fallback email in controller
        String fallbackEmail = "testUser@gmail.com";

        User user = new User();
        user.setId("user1");
        user.setEmail(fallbackEmail);

        NotificationResponse resp = NotificationResponse.builder()
                .id("notif1")
                .userId("user1")
                .title("Test Title")
                .message("Test Message")
                .read(false)
                .createdAt(Instant.now())
                .build();

        when(userRepository.findByEmail(fallbackEmail))
                .thenReturn(Optional.of(user));

        when(notificationService.getByUser("user1"))
                .thenReturn(List.of(resp));

        mockMvc.perform(get("/api/notifications/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("notif1"));
    }

    // ================= ADMIN GET USER =================
    @Test
    void getByUser_shouldReturnList() throws Exception {

        NotificationResponse resp = NotificationResponse.builder()
                .id("notif1")
                .userId("user1")
                .title("Admin Fetch")
                .message("Admin Message")
                .read(false)
                .createdAt(Instant.now())
                .build();

        when(notificationService.getByUser("user1"))
                .thenReturn(List.of(resp));

        mockMvc.perform(get("/api/notifications/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Admin Fetch"));
    }

    // ================= MARK AS READ =================
    @Test
    void markAsRead_shouldReturnResponse() throws Exception {

        doNothing().when(notificationService).markAsRead("notif1");

        mockMvc.perform(put("/api/notifications/notif1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("SUCCESS"));
    }
}

