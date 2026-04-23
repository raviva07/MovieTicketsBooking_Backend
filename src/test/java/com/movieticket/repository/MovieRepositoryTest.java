package com.movieticket.repository;

import com.movieticket.entity.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@DataMongoTest
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();

        movie = new Movie();
        movie.setTitle("Avengers");
        movie.setGenres(Set.of("Action")); // ✅ FIX
        movie = movieRepository.save(movie);
    }

    @Test
    void testFindById() {
        Optional<Movie> found = movieRepository.findById(movie.getId());
        assertTrue(found.isPresent());
        assertEquals("Avengers", found.get().getTitle());
    }

    @Test
    void testSaveAndDelete() {
        Movie m = new Movie();
        m.setTitle("Inception");
        m.setGenres(Set.of("Sci-Fi")); // ✅ FIX
        m = movieRepository.save(m);

        Optional<Movie> found = movieRepository.findById(m.getId());
        assertTrue(found.isPresent());

        movieRepository.delete(m);
        assertFalse(movieRepository.findById(m.getId()).isPresent());
    }
}

