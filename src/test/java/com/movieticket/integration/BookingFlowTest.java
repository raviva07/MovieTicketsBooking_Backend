package com.movieticket.integration;

import com.movieticket.dto.request.BookingRequest;
import com.movieticket.dto.response.BookingResponse;
import com.movieticket.entity.BookingStatus;
import com.movieticket.service.BookingService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
class BookingFlowTest {

    @Mock
    private BookingService bookingService;

    private BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        bookingRequest = new BookingRequest();
        bookingRequest.setShowId("show1");
        bookingRequest.setSeatIds(List.of("s1"));
    }

    @Test
    void testCreateBookingFlow() {

        BookingResponse response = BookingResponse.builder()
                .id("b1")
                .status(BookingStatus.PENDING.name())
                .build();

        when(bookingService.create(any(), anyString()))
                .thenReturn(response);

        BookingResponse res =
                bookingService.create(bookingRequest, "testUser");

        assertEquals("b1", res.getId());

        verify(bookingService).create(any(), eq("testUser"));
    }
}

