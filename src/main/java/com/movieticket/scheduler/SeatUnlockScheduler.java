package com.movieticket.scheduler;

import com.movieticket.entity.Booking;
import com.movieticket.entity.BookingStatus;
import com.movieticket.repository.BookingRepository;
import com.movieticket.repository.ShowRepository;
import com.movieticket.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatUnlockScheduler {

    private final BookingRepository bookingRepository;
    private final SeatService seatService;
    private final ShowRepository showRepository;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void processBookings() {

        Instant nowInstant = Instant.now();
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());

        // ================= EXPIRED BOOKINGS =================
        List<Booking> expiredBookings =
                bookingRepository.findByStatusAndExpiresAtBefore(
                        BookingStatus.PENDING,
                        nowInstant
                );

        for (Booking booking : expiredBookings) {
            try {
                if (booking.getSeatIds() != null) {
                    seatService.releaseExpiredSeats(booking.getSeatIds());
                }

                booking.setStatus(BookingStatus.EXPIRED);
                bookingRepository.save(booking);

            } catch (Exception ex) {
                log.error("Expire error {}", booking.getId(), ex);
            }
        }

        // ================= COMPLETED BOOKINGS =================
        List<Booking> confirmedBookings =
                bookingRepository.findByStatus(BookingStatus.CONFIRMED);

        for (Booking booking : confirmedBookings) {
            try {

                var showOpt = showRepository.findById(booking.getShowId());
                if (showOpt.isEmpty()) continue;

                var show = showOpt.get();

                if (show.getEndTime() != null &&
                        show.getEndTime().isBefore(now)) {

                    booking.setStatus(BookingStatus.COMPLETED);
                    bookingRepository.save(booking);

                    if (booking.getSeatIds() != null) {
                        seatService.releaseBookedSeats(booking.getSeatIds());
                    }
                }

            } catch (Exception ex) {
                log.error("Complete error {}", booking.getId(), ex);
            }
        }
    }
}
