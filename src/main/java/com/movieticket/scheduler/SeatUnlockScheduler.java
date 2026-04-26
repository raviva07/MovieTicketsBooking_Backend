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

        // ================= 1. EXPIRE PENDING BOOKINGS =================
        List<Booking> expiredBookings =
                bookingRepository.findByStatusAndExpiresAtBefore(
                        BookingStatus.PENDING,
                        nowInstant
                );

        for (Booking booking : expiredBookings) {
            try {

                // release locked seats
                if (booking.getSeatIds() != null && !booking.getSeatIds().isEmpty()) {
                    seatService.releaseExpiredSeats(booking.getSeatIds());
                }

                booking.setStatus(BookingStatus.EXPIRED);
                bookingRepository.save(booking);

                log.info("Booking expired: {}", booking.getId());

            } catch (Exception ex) {
                log.error("Expire error {}", booking.getId(), ex);
            }
        }

        // ================= 2. RELEASE SEATS AFTER SHOW ENDS =================
        List<Booking> confirmedBookings =
                bookingRepository.findByStatus(BookingStatus.CONFIRMED);

        for (Booking booking : confirmedBookings) {
            try {

                var showOpt = showRepository.findById(booking.getShowId());
                if (showOpt.isEmpty()) continue;

                var show = showOpt.get();

                // show finished
                if (show.getEndTime() != null &&
                        show.getEndTime().isBefore(now)) {

                    // ONLY FREE SEATS (NO STATUS CHANGE)
                    if (booking.getSeatIds() != null && !booking.getSeatIds().isEmpty()) {
                        seatService.releaseBookedSeats(booking.getSeatIds());
                    }

                    log.info("Seats released for completed show booking: {}", booking.getId());
                }

            } catch (Exception ex) {
                log.error("Show completion error {}", booking.getId(), ex);
            }
        }
    }
}

