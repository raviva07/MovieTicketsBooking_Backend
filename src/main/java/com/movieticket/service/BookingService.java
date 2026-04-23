package com.movieticket.service;

import com.movieticket.dto.request.BookingRequest;
import com.movieticket.dto.response.BookingResponse;
import com.movieticket.entity.Booking;
import com.movieticket.entity.BookingStatus;
import com.movieticket.entity.Role;
import com.movieticket.exception.BadRequestException;
import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.exception.UnauthorizedException;
import com.movieticket.mapper.BookingMapper;
import com.movieticket.repository.BookingRepository;
import com.movieticket.repository.ShowRepository;
import com.movieticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private static final long BOOKING_EXPIRY_SECONDS = 300;

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ShowRepository showRepository;
    private final SeatService seatService;

    // ================= CREATE =================
    @Transactional
    public BookingResponse create(BookingRequest req, String email) {

        if (req == null || req.getSeatIds() == null || req.getSeatIds().isEmpty()) {
            throw new BadRequestException("SeatIds are required");
        }

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        showRepository.findById(req.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        // ✅ IMPORTANT: Validate LOCKED seats (NOT reserve again)
        seatService.validateLockedSeats(req.getSeatIds(), email);

        Booking booking = bookingMapper.toEntity(req);

        booking.setUserId(user.getId());
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(Instant.now());
        booking.setExpiresAt(Instant.now().plusSeconds(BOOKING_EXPIRY_SECONDS));

        Booking saved = bookingRepository.save(booking);

        log.info("Booking created: {} for user {}", saved.getId(), email);

        return bookingMapper.toResponse(saved);
    }

    // ================= GET BY ID =================
    public BookingResponse getById(String id, String email) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!booking.getUserId().equals(user.getId()) && !isAdmin) {
            throw new UnauthorizedException("Access denied");
        }

        return bookingMapper.toResponse(booking);
    }

    // ================= MY BOOKINGS =================
    public Page<BookingResponse> getMyBookings(String email, Pageable pageable) {

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByUserId(user.getId(), pageable)
                .map(bookingMapper::toResponse);
    }

    // ================= ADMIN =================
    public Page<BookingResponse> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(bookingMapper::toResponse);
    }

    // ================= CANCEL =================
    @Transactional
    public BookingResponse cancel(String id, String email) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!booking.getUserId().equals(user.getId()) && !isAdmin) {
            throw new UnauthorizedException("You cannot cancel this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Only pending bookings can be cancelled");
        }

        // ✅ release LOCKED seats
        if (booking.getSeatIds() != null && !booking.getSeatIds().isEmpty()) {
            seatService.releaseSeats(booking.getSeatIds(), email);
        }

        booking.setStatus(BookingStatus.CANCELLED);

        Booking saved = bookingRepository.save(booking);

        log.info("Booking cancelled: {}", id);

        return bookingMapper.toResponse(saved);
    }

    // ================= CONFIRM PAYMENT =================
    @Transactional
    public BookingResponse confirmPayment(String bookingId, String paymentId, String email) {

        if (paymentId == null || paymentId.isBlank()) {
            throw new BadRequestException("PaymentId required");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!booking.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("Access denied");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Invalid booking state");
        }

        // ✅ CHECK EXPIRY
        if (booking.getExpiresAt() != null &&
                booking.getExpiresAt().isBefore(Instant.now())) {

            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);

            seatService.releaseExpiredSeats(booking.getSeatIds());

            throw new BadRequestException("Booking expired");
        }

        // ✅ FINAL CONFIRM
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentId(paymentId);

        Booking saved = bookingRepository.save(booking);

        // ✅ MARK SEATS BOOKED
        if (saved.getSeatIds() != null && !saved.getSeatIds().isEmpty()) {
            seatService.markSeatsBooked(saved.getSeatIds());
        }

        log.info("Payment confirmed for booking: {}", bookingId);

        return bookingMapper.toResponse(saved);
    }
}
