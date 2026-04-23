package com.movieticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticket.config.JwtFilter;
import com.movieticket.config.TestSecurityConfig;
import com.movieticket.dto.request.ShowRequest;
import com.movieticket.dto.response.ShowResponse;
import com.movieticket.service.ShowService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@WebMvcTest(ShowController.class)
class ShowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShowService showService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createShow_Success() throws Exception {
        ShowRequest req = new ShowRequest();
        req.setStartTime(LocalDateTime.now());
        req.setEndTime(LocalDateTime.now().plusHours(2));

        ShowResponse res = ShowResponse.builder().id("show1").build();

        Mockito.when(showService.create(Mockito.any())).thenReturn(res);

        mockMvc.perform(post("/api/shows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Show created successfully"))
                .andExpect(jsonPath("$.data.id").value("show1"));
    }

    @Test
    void getById_Success() throws Exception {
        ShowResponse res = ShowResponse.builder().id("show1").build();

        Mockito.when(showService.getById("show1")).thenReturn(res);

        mockMvc.perform(get("/api/shows/show1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Show fetched successfully"))
                .andExpect(jsonPath("$.data.id").value("show1"));
    }

    @Test
    void getAll_Success() throws Exception {
        ShowResponse res = ShowResponse.builder().id("show1").build();

        Mockito.when(showService.getAll()).thenReturn(List.of(res));

        mockMvc.perform(get("/api/shows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Shows fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value("show1"));
    }

    @Test
    void update_Success() throws Exception {
        ShowRequest req = new ShowRequest();
        req.setStartTime(LocalDateTime.now());
        req.setEndTime(LocalDateTime.now().plusHours(2));

        ShowResponse res = ShowResponse.builder().id("show1").build();

        Mockito.when(showService.update(Mockito.eq("show1"), Mockito.any()))
                .thenReturn(res);

        mockMvc.perform(put("/api/shows/show1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Show updated successfully"))
                .andExpect(jsonPath("$.data.id").value("show1"));
    }

    @Test
    void delete_Success() throws Exception {
        Mockito.doNothing().when(showService).delete("show1");

        mockMvc.perform(delete("/api/shows/show1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Show deleted successfully"))
                .andExpect(jsonPath("$.data").value("SUCCESS"));
    }
}

