package com.movieticket.service;

import com.movieticket.dto.request.TheaterRequest;
import com.movieticket.dto.response.TheaterResponse;
import com.movieticket.entity.Theater;
import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.mapper.TheaterMapper;
import com.movieticket.repository.TheaterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
class TheaterServiceTest {

    @InjectMocks
    private TheaterService theaterService;

    @Mock
    private TheaterRepository theaterRepository;

    @Mock
    private TheaterMapper theaterMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTheater() {
        TheaterRequest req = new TheaterRequest();
        req.setName("Cineplex");
        req.setAddress("123 Street");
        req.setCity("City");
        req.setState("State");
        req.setCountry("Country");

        Theater entity = new Theater();
        Theater saved = new Theater();
        TheaterResponse resp = TheaterResponse.builder().id("t1").name("Cineplex").build();

        when(theaterMapper.toEntity(req)).thenReturn(entity);
        when(theaterRepository.save(entity)).thenReturn(saved);
        when(theaterMapper.toResponse(saved)).thenReturn(resp);

        TheaterResponse result = theaterService.create(req);
        assertEquals("t1", result.getId());
        assertEquals("Cineplex", result.getName());
    }

    @Test
    void testGetByIdNotFound() {
        when(theaterRepository.findById("t1")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> theaterService.getById("t1"));
    }

    @Test
    void testGetAll() {
        Theater theater = new Theater();
        TheaterResponse resp = TheaterResponse.builder().id("t1").name("Cineplex").build();

        when(theaterRepository.findAll()).thenReturn(Arrays.asList(theater));
        when(theaterMapper.toResponse(theater)).thenReturn(resp);

        List<TheaterResponse> list = theaterService.getAll();
        assertEquals(1, list.size());
        assertEquals("t1", list.get(0).getId());
    }
}

