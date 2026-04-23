package com.movieticket.service;

import com.movieticket.dto.request.NotificationRequest;
import com.movieticket.dto.response.NotificationResponse;
import com.movieticket.entity.Notification;
import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.integration.EmailService;
import com.movieticket.mapper.NotificationMapper;
import com.movieticket.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private NotificationMapper notificationMapper;
    @Mock private EmailService emailService;

    @InjectMocks private NotificationService notificationService;

    private NotificationRequest req;
    private Notification entity;
    private NotificationResponse resp;

    @BeforeEach
    void setUp() {
        req = new NotificationRequest();
        req.setUserId("user1");
        req.setTitle("Test Title");
        req.setMessage("Test Message");

        entity = Notification.builder()
                .id("notif1")
                .userId("user1")
                .title("Test Title")
                .message("Test Message")
                .read(false)
                .build();

        resp = NotificationResponse.builder()
                .id("notif1")
                .userId("user1")
                .title("Test Title")
                .message("Test Message")
                .read(false)
                .build();
    }

    @Test
    void send_shouldReturnResponse() {
        when(notificationMapper.toEntity(req)).thenReturn(entity);
        when(notificationRepository.save(entity)).thenReturn(entity);
        when(notificationMapper.toResponse(entity)).thenReturn(resp);

        NotificationResponse result = notificationService.send(req);

        assertEquals("notif1", result.getId());
        verify(emailService, times(1)).sendEmail("user1", "Test Title", "Test Message");
        verify(notificationRepository, times(1)).save(entity);
    }

    @Test
    void getByUser_shouldReturnList() {
        when(notificationRepository.findByUserId("user1")).thenReturn(List.of(entity));
        when(notificationMapper.toResponse(entity)).thenReturn(resp);

        List<NotificationResponse> list = notificationService.getByUser("user1");

        assertEquals(1, list.size());
        assertEquals("notif1", list.get(0).getId());
    }

    @Test
    void markAsRead_shouldSetReadTrue() {
        entity.setRead(false);
        when(notificationRepository.findById("notif1")).thenReturn(Optional.of(entity));

        notificationService.markAsRead("notif1");

        assertTrue(entity.isRead());
        verify(notificationRepository, times(1)).save(entity);
    }

    @Test
    void markAsRead_notFound_shouldThrow() {
        when(notificationRepository.findById("notif1")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead("notif1"));
    }

    @Test
    void markAsRead_alreadyRead_shouldNotSave() {
        entity.setRead(true);
        when(notificationRepository.findById("notif1")).thenReturn(Optional.of(entity));

        notificationService.markAsRead("notif1");

        verify(notificationRepository, never()).save(entity);
    }
}
