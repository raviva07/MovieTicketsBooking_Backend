package com.movieticket.repository;

import com.movieticket.entity.Theater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@DataMongoTest
class TheaterRepositoryTest {

    @Autowired
    private TheaterRepository theaterRepository;

    private Theater theater;

    @BeforeEach
    void setUp() {
        theaterRepository.deleteAll();

        theater = new Theater();
        theater.setName("IMAX");
        theater.setLocation(new double[]{17.3850, 78.4867}); // ✅ FIX
        theater = theaterRepository.save(theater);
    }

    @Test
    void testFindById() {
        Optional<Theater> found = theaterRepository.findById(theater.getId());
        assertTrue(found.isPresent());
        assertEquals("IMAX", found.get().getName());
    }

    @Test
    void testSaveAndDelete() {
        Theater t = new Theater();
        t.setName("PVR");
        t.setLocation(new double[]{17.4000, 78.4900}); // ✅ FIX
        t = theaterRepository.save(t);

        Optional<Theater> found = theaterRepository.findById(t.getId());
        assertTrue(found.isPresent());

        theaterRepository.delete(t);

        assertFalse(theaterRepository.findById(t.getId()).isPresent()); // ✅ FIX
    }
}
