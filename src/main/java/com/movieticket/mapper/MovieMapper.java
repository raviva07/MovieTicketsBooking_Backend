package com.movieticket.mapper;

import com.movieticket.dto.request.MovieRequest;
import com.movieticket.dto.response.MovieResponse;
import com.movieticket.entity.Movie;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MovieMapper {

    /**
     * Convert Entity → Response DTO
     */
    public MovieResponse toResponse(Movie movie) {
        if (movie == null) return null;

        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genres(movie.getGenres())
                .durationMinutes(movie.getDurationMinutes())
                // ✅ FIX: BigDecimal → Double
                .rating(movie.getRating() != null ? movie.getRating().doubleValue() : null)
                .description(movie.getDescription())
                .posterUrl(movie.getPosterUrl())
                .releaseDate(movie.getReleaseDate())
                .languages(movie.getLanguages())
                .cast(movie.getCast())
                .director(movie.getDirector())
                .active(movie.getActive())
                .build();
    }

    /**
     * Convert Request → Entity
     */
    public Movie toEntity(MovieRequest req) {
        if (req == null) return null;

        return Movie.builder()
                .title(req.getTitle())
                .genres(req.getGenres())
                .durationMinutes(req.getDurationMinutes())
                // ✅ FIX: Double → BigDecimal
                .rating(req.getRating() != null ? BigDecimal.valueOf(req.getRating()) : null)
                .description(req.getDescription())
                .posterUrl(req.getPosterUrl())
                .releaseDate(req.getReleaseDate())
                .languages(req.getLanguages())
                .cast(req.getCast())
                .director(req.getDirector())
                .active(req.getActive() != null ? req.getActive() : true)
                .build();
    }

    /**
     * Update existing entity
     */
    public void updateEntity(Movie movie, MovieRequest req) {
        if (movie == null || req == null) return;

        if (req.getTitle() != null) movie.setTitle(req.getTitle());
        if (req.getGenres() != null) movie.setGenres(req.getGenres());
        if (req.getDurationMinutes() != null) movie.setDurationMinutes(req.getDurationMinutes());

        // ✅ CLEAN FIX
        if (req.getRating() != null) {
            movie.setRating(BigDecimal.valueOf(req.getRating()));
        }

        if (req.getDescription() != null) movie.setDescription(req.getDescription());
        if (req.getPosterUrl() != null) movie.setPosterUrl(req.getPosterUrl());
        if (req.getReleaseDate() != null) movie.setReleaseDate(req.getReleaseDate());
        if (req.getLanguages() != null) movie.setLanguages(req.getLanguages());
        if (req.getCast() != null) movie.setCast(req.getCast());
        if (req.getDirector() != null) movie.setDirector(req.getDirector());
        if (req.getActive() != null) movie.setActive(req.getActive());
    }
}
