package com.movieticket.repository;

import com.movieticket.entity.Seat;
import com.movieticket.entity.SeatStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@DataMongoTest
class SeatRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    private Seat seat;

    @BeforeEach
    void setUp() {
        seatRepository.deleteAll();
        seat = new Seat();
        seat.setTheaterId("t1");
        seat.setStatus(SeatStatus.AVAILABLE);
        seat = seatRepository.save(seat);
    }

    @Test
    void testFindByTheaterId() {
        List<Seat> seats = seatRepository.findByTheaterId("t1");
        assertEquals(1, seats.size());
        assertEquals(SeatStatus.AVAILABLE, seats.get(0).getStatus());
    }

    @Test
    void testDelete() {
        seatRepository.delete(seat);
        assertFalse(seatRepository.findById(seat.getId()).isPresent());
    }
}

