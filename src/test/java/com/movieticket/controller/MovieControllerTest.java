package com.movieticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticket.config.JwtFilter;
import com.movieticket.config.TestSecurityConfig;
import com.movieticket.dto.request.MovieRequest;
import com.movieticket.dto.response.MovieResponse;
import com.movieticket.service.MovieService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    // ================= CREATE =================
    @Test
    void createMovie_Success() throws Exception {
        MovieRequest req = new MovieRequest();
        req.setTitle("Movie1");
        req.setReleaseDate(LocalDate.now());

        MovieResponse res = MovieResponse.builder().id("movie1").build();
        when(movieService.create(any(MovieRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Movie created successfully"))
                .andExpect(jsonPath("$.data.id").value("movie1"));
    }

    // ================= GET BY ID =================
    @Test
    void getById_Success() throws Exception {
        MovieResponse res = MovieResponse.builder().id("movie1").build();
        when(movieService.getById("movie1")).thenReturn(res);

        mockMvc.perform(get("/api/movies/movie1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Movie fetched successfully"))
                .andExpect(jsonPath("$.data.id").value("movie1"));
    }

    // ================= GET ALL =================
    @Test
    void getAll_Success() throws Exception {
        MovieResponse res = MovieResponse.builder().id("movie1").build();
        when(movieService.getAll()).thenReturn(List.of(res));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Movie fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value("movie1"));
    }

    // ================= UPDATE =================
    @Test
    void update_Success() throws Exception {
        MovieRequest req = new MovieRequest();
        req.setTitle("Movie1 Updated");
        req.setReleaseDate(LocalDate.now());

        MovieResponse res = MovieResponse.builder().id("movie1").build();
        when(movieService.update("movie1", req)).thenReturn(res);

        mockMvc.perform(put("/api/movies/movie1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Movie updated successfully"))
                .andExpect(jsonPath("$.data.id").value("movie1"));
    }

    // ================= DELETE =================
    @Test
    void delete_Success() throws Exception {
        doNothing().when(movieService).delete("movie1");

        mockMvc.perform(delete("/api/movies/movie1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Movie deleted successfully"))
                .andExpect(jsonPath("$.data").value("SUCCESS"));
    }
}

