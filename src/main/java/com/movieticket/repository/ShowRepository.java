package com.movieticket.repository;

import com.movieticket.entity.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepository extends MongoRepository<Show, String> {

    List<Show> findByMovieIdAndStartTimeBetween(String movieId, LocalDateTime from, LocalDateTime to);

    List<Show> findByTheaterIdAndStartTimeBetween(String theaterId, LocalDateTime from, LocalDateTime to);

    List<Show> findByMovieIdAndTheaterIdAndActiveTrue(String movieId, String theaterId);
    List<Show> findByTheaterId(String theaterId);

    Page<Show> findByTheaterIdAndStartTimeBetween(
            String theaterId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );
}
