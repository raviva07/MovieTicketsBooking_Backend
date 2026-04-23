package com.movieticket.repository;

import com.movieticket.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {

    Optional<Movie> findByTitleIgnoreCase(String title);

    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Movie> findByGenresIn(Set<String> genres, Pageable pageable);

    Page<Movie> findByActiveTrue(Pageable pageable);

    Page<Movie> findByReleaseDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    List<Movie> findTop10ByActiveTrueOrderByRatingDesc();
}

