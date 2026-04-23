package com.movieticket.repository;

import com.movieticket.entity.Show;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@DataMongoTest
class ShowRepositoryTest {

    @Autowired
    private ShowRepository showRepository;

    private Show show;

    @BeforeEach
    void setUp() {
        showRepository.deleteAll();

        show = new Show();
        show.setMovieId("m1");
        show.setTheaterId("t1");
        show.setStartTime(LocalDateTime.now().plusHours(1)); // ✅ FIX
        show.setEndTime(LocalDateTime.now().plusHours(3));  // ✅ FIX
        show = showRepository.save(show);
    }

    @Test
    void testFindById() {
        Optional<Show> found = showRepository.findById(show.getId());
        assertTrue(found.isPresent());
        assertEquals("m1", found.get().getMovieId());
    }

    @Test
    void testDelete() {
        showRepository.delete(show);
        assertFalse(showRepository.findById(show.getId()).isPresent());
    }
}
