package com.movieticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticket.config.JwtFilter;
import com.movieticket.config.TestSecurityConfig;
import com.movieticket.dto.request.SeatReserveRequest;
import com.movieticket.dto.response.SeatResponse;
import com.movieticket.mapper.SeatMapper;
import com.movieticket.service.SeatService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(SeatController.class)
@Import(TestSecurityConfig.class)
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeatService seatService;

    @MockBean
    private SeatMapper seatMapper;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    // ================= GET =================
    @Test
    void getSeatsByTheater_Success() throws Exception {

        SeatResponse res = SeatResponse.builder()
                .id("seat1")
                .seatNumber("A1")
                .status("AVAILABLE")
                .build();

        Mockito.when(seatService.getSeatsByTheater("theater1"))
                .thenReturn(List.of(res));

        mockMvc.perform(get("/api/seats/theater/theater1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("seat1"));
    }

    // ================= LOCK (RESERVE) =================
    @Test
    void lockSeats_Success() throws Exception {

        SeatReserveRequest req = new SeatReserveRequest();
        req.setShowId("show1");          // IMPORTANT (was missing in earlier runs)
        req.setUserId("user1");          // optional but keeps consistency
        req.setSeatIds(List.of("seat1"));

        SeatResponse res = SeatResponse.builder()
                .id("seat1")
                .seatNumber("A1")
                .status("LOCKED")
                .build();

        // 🔥 FIX: do NOT strictly bind username
        Mockito.when(seatService.lockSeats(
                Mockito.any(SeatReserveRequest.class),
                Mockito.anyString()
        )).thenReturn(List.of(res));

        mockMvc.perform(post("/api/seats/reserve")
                        .with(user("user1").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Seats locked successfully"))
                .andExpect(jsonPath("$.data[0].id").value("seat1"))
                .andExpect(jsonPath("$.data[0].status").value("LOCKED"));
    }




    // ================= CONFIRM =================
    @Test
    void confirmSeats_Success() throws Exception {

        // ✅ VOID METHOD → use doNothing()
        Mockito.doNothing().when(seatService)
                .confirmSeats(Mockito.any(), Mockito.eq("user1"));

        mockMvc.perform(post("/api/seats/confirm")
                        .with(user("user1").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("seat1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Seats booked successfully"));
    }

    // ================= RELEASE =================
    @Test
    void releaseSeats_Success() throws Exception {

        // ✅ VOID METHOD → use doNothing()
        Mockito.doNothing().when(seatService)
                .releaseSeats(Mockito.any(), Mockito.eq("user1"));

        mockMvc.perform(post("/api/seats/release")
                        .with(user("user1").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("seat1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Seats released successfully"));
    }
}
