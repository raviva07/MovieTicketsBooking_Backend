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
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
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

    @Mock
    private NotificationService notificationService; // ✅ FIX

    @InjectMocks
    private BookingService bookingService;

    private BookingRequest request;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new BookingRequest();
        request.setShowId("show1");
        request.setSeatIds(List.of("seat1", "seat2"));

        user = new User();
        user.setId("user1");
        user.setEmail("testUser");
        user.setRole(Role.ROLE_USER);
    }

    // ================= CREATE =================
    @Test
    void createBooking_Success() {

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

        doNothing().when(seatService)
                .validateLockedSeats(request.getSeatIds(), "testUser");

        when(bookingMapper.toEntity(request)).thenReturn(booking);
        when(bookingRepository.save(any())).thenReturn(saved);
        when(bookingMapper.toResponse(saved)).thenReturn(response);

        // 🔔 avoid real notification logic
        when(notificationService.send(any())).thenReturn(null);

        BookingResponse result = bookingService.create(request, "testUser");

        assertEquals("b1", result.getId());

        verify(seatService).validateLockedSeats(request.getSeatIds(), "testUser");
        verify(notificationService, times(1)).send(any()); // ✅ important
    }

    // ================= GET BY ID =================
    @Test
    void getById_Success() {

        Booking booking = new Booking();
        booking.setId("b1");
        booking.setUserId("user1");

        BookingResponse response = BookingResponse.builder()
                .id("b1")
                .build();

        when(bookingRepository.findById("b1"))
                .thenReturn(Optional.of(booking));

        when(userRepository.findByEmail("testUser"))
                .thenReturn(Optional.of(user));

        when(bookingMapper.toResponse(booking))
                .thenReturn(response);

        BookingResponse result = bookingService.getById("b1", "testUser");

        assertEquals("b1", result.getId());
    }

    // ================= GET BY ID UNAUTHORIZED =================
    @Test
    void getById_Unauthorized() {

        Booking booking = new Booking();
        booking.setUserId("otherUser");

        when(bookingRepository.findById("b1"))
                .thenReturn(Optional.of(booking));

        when(userRepository.findByEmail("testUser"))
                .thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class,
                () -> bookingService.getById("b1", "testUser"));
    }

    // ================= CANCEL =================
    @Test
    void cancelBooking_Success() {

        Booking booking = new Booking();
        booking.setId("b1");
        booking.setUserId("user1");
        booking.setStatus(BookingStatus.PENDING);
        booking.setSeatIds(List.of("seat1"));

        Booking saved = new Booking();
        saved.setId("b1");

        BookingResponse response = BookingResponse.builder()
                .id("b1")
                .status("CANCELLED")
                .build();

        when(bookingRepository.findById("b1"))
                .thenReturn(Optional.of(booking));

        when(userRepository.findByEmail("testUser"))
                .thenReturn(Optional.of(user));

        doNothing().when(seatService)
                .releaseSeats(booking.getSeatIds(), "testUser");

        when(bookingRepository.save(any())).thenReturn(saved);
        when(bookingMapper.toResponse(saved)).thenReturn(response);

        when(notificationService.send(any())).thenReturn(null);

        BookingResponse result = bookingService.cancel("b1", "testUser");

        assertEquals("b1", result.getId());

        verify(seatService).releaseSeats(any(), eq("testUser"));
        verify(notificationService).send(any());
    }

    // ================= CONFIRM PAYMENT =================
    @Test
    void confirmPayment_Success() {

        Booking booking = new Booking();
        booking.setId("b1");
        booking.setStatus(BookingStatus.PENDING);
        booking.setSeatIds(List.of("seat1"));
        booking.setUserId("user1");
        booking.setExpiresAt(Instant.now().plusSeconds(1000));

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

        when(bookingRepository.save(any())).thenReturn(saved);
        when(bookingMapper.toResponse(saved)).thenReturn(response);

        doNothing().when(seatService)
                .markSeatsBooked(saved.getSeatIds());

        when(notificationService.send(any())).thenReturn(null);

        BookingResponse result =
                bookingService.confirmPayment("b1", "pay123", "testUser");

        assertEquals("CONFIRMED", result.getStatus());

        verify(seatService).markSeatsBooked(saved.getSeatIds());
        verify(notificationService).send(any());
    }

    // ================= EXPIRED =================
    @Test
    void confirmPayment_Expired() {

        Booking booking = new Booking();
        booking.setId("b1");
        booking.setStatus(BookingStatus.PENDING);
        booking.setUserId("user1");
        booking.setSeatIds(List.of("seat1"));
        booking.setExpiresAt(Instant.now().minusSeconds(10)); // expired

        when(bookingRepository.findById("b1"))
                .thenReturn(Optional.of(booking));

        when(userRepository.findByEmail("testUser"))
                .thenReturn(Optional.of(user));

        when(notificationService.send(any())).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> bookingService.confirmPayment("b1", "pay123", "testUser"));

        verify(seatService).releaseExpiredSeats(booking.getSeatIds());
        verify(notificationService).send(any());
    }
}

