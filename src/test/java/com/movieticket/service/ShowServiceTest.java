package com.movieticket.service;

import com.movieticket.dto.request.ShowRequest;
import com.movieticket.dto.response.ShowResponse;
import com.movieticket.entity.Show;
import com.movieticket.exception.BadRequestException;
import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.mapper.ShowMapper;
import com.movieticket.repository.ShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
class ShowServiceTest {

    @InjectMocks
    private ShowService showService;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private ShowMapper showMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSuccess() {
        ShowRequest req = new ShowRequest();
        req.setMovieId("m1");
        req.setTheaterId("t1");
        req.setStartTime(LocalDateTime.now().plusHours(1));
        req.setEndTime(LocalDateTime.now().plusHours(2));
        req.setDefaultPrice(new BigDecimal("150.0"));

        Show entity = new Show();
        Show saved = new Show();
        ShowResponse resp = ShowResponse.builder().id("s1").build();

        when(showMapper.toEntity(req)).thenReturn(entity);
        when(showRepository.save(entity)).thenReturn(saved);
        when(showMapper.toResponse(saved)).thenReturn(resp);

        ShowResponse result = showService.create(req);
        assertEquals("s1", result.getId());
    }

    @Test
    void testCreateInvalidTime() {
        ShowRequest req = new ShowRequest();
        req.setStartTime(LocalDateTime.now().plusHours(2));
        req.setEndTime(LocalDateTime.now().plusHours(1));

        assertThrows(BadRequestException.class, () -> showService.create(req));
    }

    @Test
    void testGetByIdNotFound() {
        when(showRepository.findById("s1")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> showService.getById("s1"));
    }

    @Test
    void testGetAll() {
        Show show = new Show();
        ShowResponse resp = ShowResponse.builder().id("s1").build();

        when(showRepository.findAll()).thenReturn(Arrays.asList(show));
        when(showMapper.toResponse(show)).thenReturn(resp);

        List<ShowResponse> list = showService.getAll();
        assertEquals(1, list.size());
        assertEquals("s1", list.get(0).getId());
    }
}

