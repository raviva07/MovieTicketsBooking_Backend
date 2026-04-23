package com.movieticket.repository;

import com.movieticket.entity.Booking;
import com.movieticket.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;



@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    /**
     * User booking history
     */
    Page<Booking> findByUserId(String userId, Pageable pageable);

    /**
     * Find bookings by status (for scheduler / admin)
     */
    List<Booking> findByStatus(BookingStatus status);

    /**
     * Expired bookings (seat unlock logic)
     */
    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus status, Instant time);

    /**
     * Bookings for a specific show
     */
    List<Booking> findByShowId(String showId);

    /**
     * Find booking by payment reference
     */
    Optional<Booking> findByPaymentId(String paymentId);
    
    List<Booking> findByUserId(String userId);
    // ✅ For user bookings (pagination)
   
    // ✅ Admin filtering
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    // ✅ Expired bookings cleanup
   

}
