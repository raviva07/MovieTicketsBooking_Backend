package com.movieticket.mapper;

import com.movieticket.dto.request.BookingRequest;
import com.movieticket.dto.response.BookingResponse;
import com.movieticket.entity.Booking;
import com.movieticket.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final SeatService seatService;

    public BookingResponse toResponse(Booking booking) {
        if (booking == null) return null;

        List<String> seatIds = booking.getSeatIds() != null ? booking.getSeatIds() : Collections.emptyList();

        // obtain id->seatNumber map via helper so the local variable used in the lambda is effectively final
        final Map<String, String> idToNumber = safeMapIdToSeatNumber(seatIds);

        List<String> seatNumbers = seatIds.stream()
                .map(id -> idToNumber.getOrDefault(id, id))
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .showId(booking.getShowId())
                .theaterId(booking.getTheaterId())
                .seatIds(seatIds)
                .seatNumbers(seatNumbers)
                .totalAmount(booking.getTotalAmount())
                .currency(booking.getCurrency())
                .paymentId(booking.getPaymentId())
                .status(booking.getStatus() != null ? booking.getStatus().name() : null)
                .createdAt(booking.getCreatedAt())
                .expiresAt(booking.getExpiresAt())
                .notes(booking.getNotes())
                .build();
    }

    private Map<String, String> safeMapIdToSeatNumber(List<String> seatIds) {
        try {
            Map<String, String> map = seatService.mapIdToSeatNumber(seatIds);
            return map != null ? map : Collections.emptyMap();
        } catch (Exception e) {
            // fallback to empty map so callers will use ids as fallback display values
            return Collections.emptyMap();
        }
    }

    public Booking toEntity(BookingRequest req) {
        if (req == null) return null;

        return Booking.builder()
                .userId(req.getUserId())
                .showId(req.getShowId())
                .theaterId(req.getTheaterId())
                .seatIds(req.getSeatIds())
                .totalAmount(req.getTotalAmount())
                .notes(req.getNotes())
                .build();
    }

    public void updateEntity(Booking booking, BookingRequest req) {
        if (booking == null || req == null) return;

        if (req.getSeatIds() != null) booking.setSeatIds(req.getSeatIds());
        if (req.getTotalAmount() != null) booking.setTotalAmount(req.getTotalAmount());
        if (req.getNotes() != null) booking.setNotes(req.getNotes());
    }
}

