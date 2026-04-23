package com.movieticket.service;

import com.movieticket.dto.request.BookingRequest;
import com.movieticket.dto.response.BookingResponse;
import com.movieticket.entity.*;
import com.movieticket.mapper.BookingMapper;
import com.movieticket.repository.BookingRepository;
import com.movieticket.repository.ShowRepository;
import com.movieticket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private SeatService seatService;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new BookingRequest();
        request.setShowId("show1");
        request.setSeatIds(List.of("seat1", "seat2"));
    }

    // ================= CREATE =================
    @Test
    void createBooking_Success() {

        User user = new User();
        user.setId("user1");
        user.setEmail("testUser");

        Booking booking = new Booking();
        Booking saved = new Booking();
        saved.setId("b1");

        BookingResponse response = BookingResponse.builder()
                .id("b1")
                .status("PENDING")
                .build();

        when(userRepository.findByEmail("testUser"))
                .thenReturn(Optional.of(user));

        when(showRepository.findById("show1"))
                .thenReturn(Optional.of(new Show()));

        // ✅ NEW FLOW → validate instead of lock
        doNothing().when(seatService)
                .validateLockedSeats(request.getSeatIds(), "testUser");

        when(bookingMapper.toEntity(request)).thenReturn(booking);
        when(bookingRepository.save(any())).thenReturn(saved);
        when(bookingMapper.toResponse(saved)).thenReturn(response);

        BookingResponse result = bookingService.create(request, "testUser");

        assertEquals("b1", result.getId());

        verify(seatService)
                .validateLockedSeats(request.getSeatIds(), "testUser");
    }

    // ================= CONFIRM PAYMENT =================
    @Test
    void confirmPayment_Success() {

        Booking booking = new Booking();
        booking.setId("b1");
        booking.setStatus(BookingStatus.PENDING);
        booking.setSeatIds(List.of("seat1"));
        booking.setUserId("user1");

        User user = new User();
        user.setId("user1"); // ✅ IMPORTANT FIX

        Booking saved = new Booking();
        saved.setId("b1");
        saved.setStatus(BookingStatus.CONFIRMED);
        saved.setSeatIds(List.of("seat1"));

        BookingResponse response = BookingResponse.builder()
                .id("b1")
                .status("CONFIRMED")
                .build();

        when(bookingRepository.findById("b1"))
                .thenReturn(Optional.of(booking));

        when(userRepository.findByEmail("testUser"))
                .thenReturn(Optional.of(user));

        when(bookingRepository.save(any()))
                .thenReturn(saved);

        when(bookingMapper.toResponse(saved))
                .thenReturn(response);

        doNothing().when(seatService)
                .markSeatsBooked(saved.getSeatIds());

        BookingResponse result =
                bookingService.confirmPayment("b1", "pay123", "testUser");

        assertEquals("CONFIRMED", result.getStatus());

        verify(seatService).markSeatsBooked(saved.getSeatIds());
    }
}
