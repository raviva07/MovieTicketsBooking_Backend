package com.movieticket.repository;

import com.movieticket.entity.Booking;
import com.movieticket.entity.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@DataMongoTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        booking = new Booking();
        booking.setUserId("u1");
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(Instant.now());
        booking.setExpiresAt(Instant.now().plusSeconds(300));
        booking = bookingRepository.save(booking);
    }

    @Test
    void testFindByUserId() {
        Page<Booking> bookings = bookingRepository.findByUserId("u1", PageRequest.of(0, 10));
        assertEquals(1, bookings.getTotalElements());
        assertEquals("u1", bookings.getContent().get(0).getUserId());
    }

    @Test
    void testFindById() {
        Booking found = bookingRepository.findById(booking.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(BookingStatus.PENDING, found.getStatus());
    }

    @Test
    void testDeleteBooking() {
        bookingRepository.delete(booking);
        assertFalse(bookingRepository.findById(booking.getId()).isPresent());
    }
}

