package com.movieticket.repository;

import com.movieticket.entity.Theater;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TheaterRepository extends MongoRepository<Theater, String> {

    Optional<Theater> findByNameIgnoreCase(String name);

    Page<Theater> findByCityIgnoreCase(String city, Pageable pageable);

    Page<Theater> findByCityIgnoreCaseAndActiveTrue(String city, Pageable pageable);
}

