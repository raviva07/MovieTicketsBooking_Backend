package com.movieticket.controller;

import com.movieticket.dto.request.BookingRequest;
import com.movieticket.dto.response.ApiResponse;
import com.movieticket.dto.response.BookingResponse;
import com.movieticket.service.BookingService;
import com.movieticket.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // SAFE USERNAME HANDLER
    private String getUsername(Authentication auth) {
        return (auth != null) ? auth.getName() : "testUser";
    }

    // ================= CREATE =================
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<BookingResponse> create(
            @RequestBody BookingRequest req,
            Authentication auth
    ) {
        return ApiResponse.<BookingResponse>builder()
                .success(true)
                .message(Constants.BOOKING_CREATED)
                .data(bookingService.create(req, getUsername(auth)))
                .build();
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ApiResponse<BookingResponse> getById(
            @PathVariable String id,
            Authentication auth
    ) {
        return ApiResponse.<BookingResponse>builder()
                .success(true)
                .message(Constants.BOOKING_FETCHED)
                .data(bookingService.getById(id, getUsername(auth)))
                .build();
    }

    // ================= MY BOOKINGS =================
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Page<BookingResponse>> getMyBookings(
            Authentication auth,
            Pageable pageable
    ) {
        return ApiResponse.<Page<BookingResponse>>builder()
                .success(true)
                .message(Constants.MY_BOOKINGS_FETCHED)
                .data(bookingService.getMyBookings(getUsername(auth), pageable))
                .build();
    }

    // ================= ADMIN: ALL BOOKINGS =================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<BookingResponse>> getAllBookings(Pageable pageable) {
        return ApiResponse.<Page<BookingResponse>>builder()
                .success(true)
                .message(Constants.ALL_BOOKINGS_FETCHED)
                .data(bookingService.getAllBookings(pageable))
                .build();
    }

    // ================= CANCEL =================
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ApiResponse<BookingResponse> cancel(
            @PathVariable String id,
            Authentication auth
    ) {
        BookingResponse updated = bookingService.cancel(id, getUsername(auth));

        return ApiResponse.<BookingResponse>builder()
                .success(true)
                .message(Constants.BOOKING_CANCELLED)
                .data(updated)
                .build();
    }

    // ================= CONFIRM PAYMENT =================
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<BookingResponse> confirmPayment(
            @PathVariable String id,
            @RequestParam String paymentId,
            Authentication auth
    ) {
        BookingResponse updated = bookingService.confirmPayment(id, paymentId, getUsername(auth));

        return ApiResponse.<BookingResponse>builder()
                .success(true)
                .message(Constants.BOOKING_PAYMENT_CONFIRMED)
                .data(updated)
                .build();
    }
}
