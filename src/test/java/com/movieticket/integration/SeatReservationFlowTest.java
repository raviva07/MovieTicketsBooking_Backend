package com.movieticket.integration;

import com.movieticket.dto.request.SeatReserveRequest;
import com.movieticket.dto.response.SeatResponse;
import com.movieticket.entity.Seat;
import com.movieticket.entity.SeatStatus;
import com.movieticket.mapper.SeatMapper;
import com.movieticket.repository.SeatRepository;
import com.movieticket.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
class SeatReservationFlowTest {

    @InjectMocks
    private SeatService seatService;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatMapper seatMapper;

    private SeatReserveRequest reserveRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        reserveRequest = new SeatReserveRequest();
        reserveRequest.setSeatIds(List.of("seat1", "seat2"));
    }

    @Test
    void testLockAndConfirmSeatsFlow() {

        // ===== MOCK SEATS =====
        Seat seat1 = new Seat();
        seat1.setId("seat1");
        seat1.setStatus(SeatStatus.AVAILABLE);

        Seat seat2 = new Seat();
        seat2.setId("seat2");
        seat2.setStatus(SeatStatus.AVAILABLE);

        List<Seat> seatList = List.of(seat1, seat2);

        when(seatRepository.findAllById(reserveRequest.getSeatIds()))
                .thenReturn(seatList);

        when(seatRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(seatMapper.toResponse(any()))
                .thenAnswer(invocation -> {
                    Seat s = invocation.getArgument(0);
                    return SeatResponse.builder()
                            .id(s.getId())
                            .status(s.getStatus().name())
                            .build();
                });

        // ===== STEP 1: LOCK SEATS =====
        List<SeatResponse> locked =
                seatService.lockSeats(reserveRequest, "testUser");

        assertEquals(2, locked.size());
        assertEquals(SeatStatus.LOCKED, seat1.getStatus());
        assertEquals("testUser", seat1.getLockedBy());

        // ===== SET LOCK DETAILS (simulate persistence state) =====
        seat1.setLockedAt(Instant.now());
        seat2.setLockedAt(Instant.now());

        // ===== STEP 2: CONFIRM SEATS =====
        seatService.confirmSeats(reserveRequest.getSeatIds(), "testUser");

        assertEquals(SeatStatus.BOOKED, seat1.getStatus());
        assertEquals(SeatStatus.BOOKED, seat2.getStatus());
        assertNull(seat1.getLockedBy());
        assertNull(seat2.getLockedBy());
    }
}

