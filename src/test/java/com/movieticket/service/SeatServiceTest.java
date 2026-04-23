package com.movieticket.service;

import com.movieticket.dto.request.SeatReserveRequest;
import com.movieticket.dto.response.SeatResponse;
import com.movieticket.entity.Seat;
import com.movieticket.entity.SeatStatus;
import com.movieticket.exception.SeatUnavailableException;
import com.movieticket.exception.UnauthorizedException;
import com.movieticket.mapper.SeatMapper;
import com.movieticket.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
class SeatServiceTest {

    @InjectMocks
    private SeatService seatService;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatMapper seatMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ================= LOCK SEATS =================
    @Test
    void testLockSeatsSuccess() {

        Seat seat = new Seat();
        seat.setId("seat1");
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.AVAILABLE);

        SeatReserveRequest req = new SeatReserveRequest();
        req.setSeatIds(List.of("seat1"));

        when(seatRepository.findAllById(req.getSeatIds()))
                .thenReturn(List.of(seat));

        when(seatRepository.saveAll(anyList()))
                .thenReturn(List.of(seat));

        when(seatMapper.toResponse(any()))
                .thenReturn(SeatResponse.builder()
                        .id("seat1")
                        .status("LOCKED")
                        .build());

        List<SeatResponse> result =
                seatService.lockSeats(req, "user1");

        assertEquals(1, result.size());
        assertEquals(SeatStatus.LOCKED, seat.getStatus());
        assertEquals("user1", seat.getLockedBy());
    }

    @Test
    void testLockSeatsUnavailable() {

        Seat seat = new Seat();
        seat.setId("seat1");
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.BOOKED);

        SeatReserveRequest req = new SeatReserveRequest();
        req.setSeatIds(List.of("seat1"));

        when(seatRepository.findAllById(req.getSeatIds()))
                .thenReturn(List.of(seat));

        assertThrows(SeatUnavailableException.class,
                () -> seatService.lockSeats(req, "user1"));
    }

    // ================= CONFIRM SEATS =================
    @Test
    void testConfirmSeatsSuccess() {

        Seat seat = new Seat();
        seat.setId("seat1");
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.LOCKED);
        seat.setLockedBy("user1");
        seat.setLockedAt(Instant.now());

        when(seatRepository.findAllById(List.of("seat1")))
                .thenReturn(List.of(seat));

        when(seatRepository.saveAll(anyList()))
                .thenReturn(List.of(seat));

        seatService.confirmSeats(List.of("seat1"), "user1");

        assertEquals(SeatStatus.BOOKED, seat.getStatus());
        assertNull(seat.getLockedBy());
    }

    @Test
    void testConfirmSeatsUnauthorized() {

        Seat seat = new Seat();
        seat.setId("seat1");
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.LOCKED);
        seat.setLockedBy("otherUser");
        seat.setLockedAt(Instant.now());

        when(seatRepository.findAllById(List.of("seat1")))
                .thenReturn(List.of(seat));

        assertThrows(UnauthorizedException.class,
                () -> seatService.confirmSeats(List.of("seat1"), "user1"));
    }

    // ================= RELEASE =================
    @Test
    void testReleaseSeatsSuccess() {

        Seat seat = new Seat();
        seat.setId("seat1");
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.LOCKED);
        seat.setLockedBy("user1");

        when(seatRepository.findAllById(List.of("seat1")))
                .thenReturn(List.of(seat));

        when(seatRepository.saveAll(anyList()))
                .thenReturn(List.of(seat));

        seatService.releaseSeats(List.of("seat1"), "user1");

        assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
        assertNull(seat.getLockedBy());
    }

    @Test
    void testReleaseSeatsUnauthorized() {

        Seat seat = new Seat();
        seat.setId("seat1");
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.LOCKED);
        seat.setLockedBy("otherUser");

        when(seatRepository.findAllById(List.of("seat1")))
                .thenReturn(List.of(seat));

        assertThrows(UnauthorizedException.class,
                () -> seatService.releaseSeats(List.of("seat1"), "user1"));
    }
}

