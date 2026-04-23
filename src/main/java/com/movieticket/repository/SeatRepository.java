package com.movieticket.repository;

import com.movieticket.entity.Seat;
import com.movieticket.entity.SeatStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends MongoRepository<Seat, String> {

    Optional<Seat> findByTheaterIdAndSeatNumber(String theaterId, String seatNumber);

    List<Seat> findByTheaterId(String theaterId);

    Page<Seat> findByTheaterId(String theaterId, Pageable pageable);

    List<Seat> findByTheaterIdAndStatus(String theaterId, SeatStatus status);

    boolean existsByTheaterIdAndSeatNumber(String theaterId, String seatNumber);
    List<Seat> findByStatusAndLockedAtBefore(SeatStatus status, Instant time);
    List<Seat> findByIdIn(List<String> ids);
    List<Seat> findByShowIdAndSeatNumberIn(String showId, List<String> seatNumbers);
}
