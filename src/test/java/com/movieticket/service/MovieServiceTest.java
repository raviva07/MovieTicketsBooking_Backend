package com.movieticket.service;

import com.movieticket.dto.request.MovieRequest;
import com.movieticket.dto.response.MovieResponse;
import com.movieticket.entity.Movie;
import com.movieticket.integration.CloudinaryService;
import com.movieticket.mapper.MovieMapper;
import com.movieticket.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private MovieService movieService;

    @Test
    void create_shouldSaveMovie() {
        MovieRequest req = new MovieRequest();
        Movie movie = new Movie();
        Movie saved = new Movie();
        MovieResponse res = MovieResponse.builder().id("1").build();

        when(movieMapper.toEntity(req)).thenReturn(movie);
        when(movieRepository.save(movie)).thenReturn(saved);
        when(movieMapper.toResponse(saved)).thenReturn(res);

        MovieResponse result = movieService.create(req);

        assertNotNull(result);
        verify(movieRepository).save(movie);
    }

    @Test
    void getById_shouldReturnMovie() {
        Movie movie = new Movie();
        MovieResponse res = MovieResponse.builder().id("1").build();

        when(movieRepository.findById("1")).thenReturn(Optional.of(movie));
        when(movieMapper.toResponse(movie)).thenReturn(res);

        MovieResponse result = movieService.getById("1");

        assertEquals("1", result.getId());
    }

    @Test
    void getAll_shouldReturnList() {
        Movie movie = new Movie();

        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(movieMapper.toResponse(movie))
                .thenReturn(MovieResponse.builder().id("1").build());

        List<MovieResponse> result = movieService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void delete_shouldCallRepo() {
        when(movieRepository.existsById("1")).thenReturn(true);

        movieService.delete("1");

        verify(movieRepository).deleteById("1");
    }

    @Test
    void uploadPoster_shouldReturnUrl() throws Exception {
        MultipartFile file = mock(MultipartFile.class);

        when(cloudinaryService.uploadFile(file)).thenReturn("url");

        String result = movieService.uploadPoster(file);

        assertEquals("url", result);
    }
}

