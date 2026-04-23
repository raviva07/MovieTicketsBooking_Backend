package com.movieticket.service;

import com.movieticket.dto.request.SeatReserveRequest;
import com.movieticket.dto.response.SeatResponse;
import com.movieticket.entity.Seat;
import com.movieticket.entity.SeatStatus;
import com.movieticket.exception.BadRequestException;
import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.exception.SeatUnavailableException;
import com.movieticket.exception.UnauthorizedException;
import com.movieticket.mapper.SeatMapper;
import com.movieticket.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatService {

    private static final long LOCK_DURATION_SECONDS = 300; // 5 minutes

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;

    // ================= FIND =================
    public List<Seat> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return seatRepository.findByIdIn(ids);
    }

    // ================= MAP =================
    public Map<String, String> mapIdToSeatNumber(List<String> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) return Collections.emptyMap();

        return findByIds(seatIds).stream()
                .collect(Collectors.toMap(
                        Seat::getId,
                        s -> Optional.ofNullable(s.getSeatNumber()).orElse(s.getId())
                ));
    }

    // ================= LOCK SEATS =================
    @Transactional
    public List<SeatResponse> lockSeats(SeatReserveRequest req, String username) {

        if (req == null || req.getSeatIds() == null || req.getSeatIds().isEmpty()) {
            throw new ResourceNotFoundException("Seat IDs required");
        }

        List<String> seatIds = req.getSeatIds();
        List<Seat> seats = seatRepository.findAllById(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new ResourceNotFoundException("Some seats not found");
        }

        Instant now = Instant.now();

        for (Seat seat : seats) {

            // 🔥 Handle expired locks
            if (seat.getStatus() == SeatStatus.LOCKED &&
                    seat.getLockedAt() != null &&
                    seat.getLockedAt().plusSeconds(LOCK_DURATION_SECONDS).isBefore(now)) {

                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setLockedBy(null);
                seat.setLockedAt(null);
            }

            // ❌ Already booked
            if (seat.getStatus() == SeatStatus.BOOKED) {
                throw new SeatUnavailableException("Seat already booked: " + seat.getSeatNumber());
            }

            // ❌ Locked by another user
            if (seat.getStatus() == SeatStatus.LOCKED &&
                    !username.equals(seat.getLockedBy())) {
                throw new SeatUnavailableException("Seat locked by another user: " + seat.getSeatNumber());
            }

            // ✅ Lock seat
            seat.setStatus(SeatStatus.LOCKED);
            seat.setLockedBy(username);
            seat.setLockedAt(now);
        }

        seatRepository.saveAll(seats);

        log.info("Locked {} seats for {}", seats.size(), username);

        return seats.stream()
                .map(seatMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ================= CONFIRM BOOKING =================
    @Transactional
    public void confirmSeats(List<String> seatIds, String username) {

        if (seatIds == null || seatIds.isEmpty()) {
            throw new ResourceNotFoundException("Seat IDs required");
        }

        List<Seat> seats = seatRepository.findAllById(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new ResourceNotFoundException("Some seats not found");
        }

        Instant now = Instant.now();

        for (Seat seat : seats) {

            // ❌ Not locked
            if (seat.getStatus() != SeatStatus.LOCKED) {
                throw new SeatUnavailableException("Seat not locked: " + seat.getSeatNumber());
            }

            // ❌ Wrong user
            if (!username.equals(seat.getLockedBy())) {
                throw new UnauthorizedException("Seat not locked by you: " + seat.getSeatNumber());
            }

            // ❌ Lock expired
            if (seat.getLockedAt() == null ||
                    seat.getLockedAt().plusSeconds(LOCK_DURATION_SECONDS).isBefore(now)) {
                throw new SeatUnavailableException("Lock expired: " + seat.getSeatNumber());
            }

            // ✅ Book seat
            seat.setStatus(SeatStatus.BOOKED);
            seat.setLockedBy(null);
            seat.setLockedAt(null);
        }

        seatRepository.saveAll(seats);

        log.info("Booked {} seats for {}", seats.size(), username);
    }

    // ================= RELEASE (USER CANCEL) =================
    @Transactional
    public void releaseSeats(List<String> seatIds, String username) {

        if (seatIds == null || seatIds.isEmpty()) return;

        List<Seat> seats = seatRepository.findAllById(seatIds);

        for (Seat seat : seats) {

            if (seat.getStatus() == SeatStatus.BOOKED) continue;

            if (seat.getLockedBy() != null &&
                    !seat.getLockedBy().equals(username)) {
                throw new UnauthorizedException("Cannot release other's seat");
            }

            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLockedBy(null);
            seat.setLockedAt(null);
        }

        seatRepository.saveAll(seats);

        log.info("Released seats by {}", username);
    }

    // ================= RELEASE EXPIRED =================

    @Transactional
    public void releaseExpiredSeats(List<String> seatIds) {

        if (seatIds == null || seatIds.isEmpty()) return;

        List<Seat> seats = seatRepository.findAllById(seatIds);

        for (Seat seat : seats) {

            // ✅ FIX: ONLY release LOCKED seats
            if (seat.getStatus() == SeatStatus.LOCKED) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setLockedBy(null);
                seat.setLockedAt(null);
            }
        }

        seatRepository.saveAll(seats);

        log.info("System released {} seats", seats.size());
    }


    // ================= GET =================
    public List<SeatResponse> getSeatsByTheater(String theaterId) {

        if (theaterId == null || theaterId.isBlank()) {
            throw new ResourceNotFoundException("Theater ID required");
        }

        return seatRepository.findByTheaterId(theaterId)
                .stream()
                .map(seatMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    
    
    public void validateLockedSeats(List<String> seatIds, String username) {

        if (seatIds == null || seatIds.isEmpty()) {
            throw new ResourceNotFoundException("SeatIds required");
        }

        List<Seat> seats = seatRepository.findAllById(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new ResourceNotFoundException("Some seats not found");
        }

        for (Seat seat : seats) {

            // ✅ Must be LOCKED
            if (seat.getStatus() != SeatStatus.LOCKED) {
                throw new SeatUnavailableException("Seat not locked: " + seat.getId());
            }

            // ✅ Must be locked by same user
            if (seat.getLockedBy() == null || !seat.getLockedBy().equals(username)) {
                throw new UnauthorizedException("Seat not locked by you: " + seat.getId());
            }

            // ✅ Must not be expired
            if (seat.getLockedAt() == null ||
                    seat.getLockedAt().plusSeconds(LOCK_DURATION_SECONDS).isBefore(Instant.now())) {
                throw new SeatUnavailableException("Seat lock expired: " + seat.getId());
            }
        }
    }
    
    @Transactional
    public void markSeatsBooked(List<String> seatIds) {

        if (seatIds == null || seatIds.isEmpty()) return;

        List<Seat> seats = seatRepository.findByIdIn(seatIds);

        if (seats.isEmpty()) {
            throw new ResourceNotFoundException("Seats not found");
        }

        for (Seat seat : seats) {

            // ✅ Only LOCKED seats can be booked
            if (seat.getStatus() != SeatStatus.LOCKED) {
                throw new SeatUnavailableException("Seat not locked: " + seat.getId());
            }

            seat.setStatus(SeatStatus.BOOKED);
            seat.setLockedBy(null);
            seat.setLockedAt(null);
        }

        seatRepository.saveAll(seats);

        log.info("Marked {} seats as BOOKED", seats.size());
    }
    
    @Transactional
    public void releaseBookedSeats(List<String> seatIds) {

        if (seatIds == null || seatIds.isEmpty()) return;

        List<Seat> seats = seatRepository.findAllById(seatIds);

        for (Seat seat : seats) {

            // ✅ Only release BOOKED seats
            if (seat.getStatus() == SeatStatus.BOOKED) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setLockedBy(null);
                seat.setLockedAt(null);
            }
        }

        seatRepository.saveAll(seats);

        log.info("Released {} BOOKED seats after show completion", seats.size());
    }




}
