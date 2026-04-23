package com.movieticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticket.config.JwtFilter;
import com.movieticket.config.TestSecurityConfig;
import com.movieticket.dto.request.BookingRequest;
import com.movieticket.dto.response.BookingResponse;
import com.movieticket.entity.BookingStatus;
import com.movieticket.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(BookingController.class)
@Import(TestSecurityConfig.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ CREATE
    @Test
    void createBooking_success() throws Exception {

        BookingRequest request = new BookingRequest();
        request.setShowId("show1");

        BookingResponse response = BookingResponse.builder()
                .id("b1")
                .status(BookingStatus.PENDING.name())
                .build();

        when(bookingService.create(any(), eq("testUser")))
                .thenReturn(response);

        mockMvc.perform(post("/api/bookings")
                        .with(user("testUser").roles("USER"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("b1"));
    }

    // ✅ CANCEL
    @Test
    void cancelBooking_success() throws Exception {

        BookingResponse response = BookingResponse.builder()
                .id("b1")
                .status(BookingStatus.CANCELLED.name())
                .build();

        when(bookingService.cancel("b1", "testUser"))
                .thenReturn(response);

        mockMvc.perform(put("/api/bookings/b1/cancel")
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("b1"))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    // ✅ FIXED CONFIRM PAYMENT (IMPORTANT)
    @Test
    void confirmPayment_success() throws Exception {

        BookingResponse response = BookingResponse.builder()
                .id("b1")
                .status(BookingStatus.CONFIRMED.name())
                .build();

        // ✅ FIX: use 3 args
        when(bookingService.confirmPayment("b1", "pay123", "testUser"))
                .thenReturn(response);

        mockMvc.perform(put("/api/bookings/b1/confirm")
                        .with(user("testUser").roles("USER"))
                        .param("paymentId", "pay123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("b1"))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    // ✅ GET BY ID
    @Test
    void getBookingById_success() throws Exception {

        BookingResponse response = BookingResponse.builder()
                .id("b1")
                .status(BookingStatus.PENDING.name())
                .build();

        when(bookingService.getById("b1", "testUser"))
                .thenReturn(response);

        mockMvc.perform(get("/api/bookings/b1")
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("b1"));
    }

    // ✅ MY BOOKINGS
    @Test
    void getMyBookings_success() throws Exception {

        BookingResponse response = BookingResponse.builder()
                .id("b1")
                .status(BookingStatus.PENDING.name())
                .build();

        Page<BookingResponse> page = new PageImpl<>(List.of(response));

        when(bookingService.getMyBookings(eq("testUser"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/bookings/my")
                        .with(user("testUser").roles("USER"))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value("b1"));
    }
}
