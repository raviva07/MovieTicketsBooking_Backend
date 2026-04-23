package com.movieticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticket.config.JwtFilter;
import com.movieticket.config.TestSecurityConfig;
import com.movieticket.dto.request.TheaterRequest;
import com.movieticket.dto.response.TheaterResponse;
import com.movieticket.service.TheaterService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@WebMvcTest(TheaterController.class)
class TheaterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TheaterService theaterService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTheater_Success() throws Exception {
        TheaterRequest req = new TheaterRequest();
        req.setName("Theater1");
        req.setCity("City1");

        TheaterResponse res = TheaterResponse.builder().id("t1").build();

        Mockito.when(theaterService.create(Mockito.any())).thenReturn(res);

        mockMvc.perform(post("/api/theaters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Theater created successfully"))
                .andExpect(jsonPath("$.data.id").value("t1"));
    }

    @Test
    void getById_Success() throws Exception {
        TheaterResponse res = TheaterResponse.builder().id("t1").build();

        Mockito.when(theaterService.getById("t1")).thenReturn(res);

        mockMvc.perform(get("/api/theaters/t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Theater fetched successfully"))
                .andExpect(jsonPath("$.data.id").value("t1"));
    }

    @Test
    void getAll_Success() throws Exception {
        TheaterResponse res = TheaterResponse.builder().id("t1").build();

        Mockito.when(theaterService.getAll()).thenReturn(List.of(res));

        mockMvc.perform(get("/api/theaters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Theaters fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value("t1"));
    }

    @Test
    void update_Success() throws Exception {
        TheaterRequest req = new TheaterRequest();
        req.setName("Updated");
        req.setCity("City");

        TheaterResponse res = TheaterResponse.builder().id("t1").build();

        Mockito.when(theaterService.update(Mockito.eq("t1"), Mockito.any()))
                .thenReturn(res);

        mockMvc.perform(put("/api/theaters/t1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Theater updated successfully"))
                .andExpect(jsonPath("$.data.id").value("t1"));
    }

    @Test
    void delete_Success() throws Exception {
        Mockito.doNothing().when(theaterService).delete("t1");

        mockMvc.perform(delete("/api/theaters/t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Theater deleted successfully"))
                .andExpect(jsonPath("$.data").value("SUCCESS"));
    }
}
