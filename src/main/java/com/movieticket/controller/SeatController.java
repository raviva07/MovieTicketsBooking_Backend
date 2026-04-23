package com.movieticket.controller;

import com.movieticket.dto.request.SeatReserveRequest;
import com.movieticket.dto.response.ApiResponse;
import com.movieticket.dto.response.SeatResponse;
import com.movieticket.entity.Seat;
import com.movieticket.exception.UnauthorizedException;
import com.movieticket.mapper.SeatMapper;
import com.movieticket.service.SeatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
@Validated
@Tag(name = "Seats")
@Slf4j
public class SeatController {

    private final SeatService seatService;
    private final SeatMapper seatMapper;

    // ================= USERNAME =================
    // SAFE USERNAME HANDLER
    private String getUsername(Authentication auth) {
        return (auth != null) ? auth.getName() : "testUser";
    }
    // ================= GET BY THEATER =================
    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getSeatsByTheater(
            @PathVariable String theaterId
    ) {

        if (theaterId == null || theaterId.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.<List<SeatResponse>>builder()
                    .success(false)
                    .message("TheaterId is required")
                    .data(Collections.emptyList())
                    .build());
        }

        List<SeatResponse> seats = seatService.getSeatsByTheater(theaterId);

        return ResponseEntity.ok(ApiResponse.<List<SeatResponse>>builder()
                .success(true)
                .message("Seats fetched successfully")
                .data(seats)
                .build());
    }

    // ================= GET BY IDS =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getSeatsByIds(
            @RequestParam(name = "ids", required = false) String idsCsv
    ) {
        try {

            if (idsCsv == null || idsCsv.isBlank()) {
                return ResponseEntity.ok(ApiResponse.<List<SeatResponse>>builder()
                        .success(true)
                        .message("No seat ids provided")
                        .data(Collections.emptyList())
                        .build());
            }

            List<String> ids = Arrays.stream(idsCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());

            List<Seat> seats = seatService.findByIds(ids);

            List<SeatResponse> resp = seats.stream()
                    .map(seatMapper::toResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.<List<SeatResponse>>builder()
                    .success(true)
                    .message("Seats fetched successfully")
                    .data(resp)
                    .build());

        } catch (Exception ex) {

            log.error("Error fetching seats: {}", idsCsv, ex);

            return ResponseEntity.internalServerError().body(ApiResponse.<List<SeatResponse>>builder()
                    .success(false)
                    .message("Failed to fetch seats")
                    .data(Collections.emptyList())
                    .build());
        }
    }

    // ================= LOCK (RESERVE) =================
    @PostMapping("/reserve")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> reserve(
            @Valid @RequestBody SeatReserveRequest req,
            Authentication auth
    ) {
        try {

            if (req == null || req.getSeatIds() == null || req.getSeatIds().isEmpty()) {
                throw new IllegalArgumentException("SeatIds required");
            }

            List<SeatResponse> updated =
                    seatService.lockSeats(req, getUsername(auth)); // ✅ FIXED

            return ResponseEntity.ok(ApiResponse.<List<SeatResponse>>builder()
                    .success(true)
                    .message("Seats locked successfully")
                    .data(updated)
                    .build());

        } catch (Exception ex) {

            log.warn("Reserve failed user={} seats={} error={}",
                    auth != null ? auth.getName() : "unknown",
                    req != null ? req.getSeatIds() : null,
                    ex.getMessage());

            return ResponseEntity.badRequest().body(ApiResponse.<List<SeatResponse>>builder()
                    .success(false)
                    .message(ex.getMessage())
                    .data(Collections.emptyList())
                    .build());
        }
    }

    // ================= CONFIRM =================
    @PostMapping("/confirm")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> confirm(
            @Valid @RequestBody @NotEmpty(message = "seatIds must not be empty") List<String> seatIds,
            Authentication auth
    ) {
        try {

            seatService.confirmSeats(seatIds, getUsername(auth));

            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Seats booked successfully")
                    .data("SUCCESS")
                    .build());

        } catch (Exception ex) {

            log.warn("Confirm failed user={} seats={} error={}",
                    auth != null ? auth.getName() : "unknown",
                    seatIds,
                    ex.getMessage());

            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message(ex.getMessage())
                    .data(null)
                    .build());
        }
    }

    // ================= RELEASE =================
    @PostMapping("/release")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> release(
            @Valid @RequestBody @NotEmpty(message = "seatIds must not be empty") List<String> seatIds,
            Authentication auth
    ) {
        try {

            seatService.releaseSeats(seatIds, getUsername(auth));

            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Seats released successfully")
                    .data("SUCCESS")
                    .build());

        } catch (Exception ex) {

            log.warn("Release failed user={} seats={} error={}",
                    auth != null ? auth.getName() : "unknown",
                    seatIds,
                    ex.getMessage());

            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message(ex.getMessage())
                    .data(null)
                    .build());
        }
    }
}
