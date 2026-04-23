package com.movieticket.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class MovieResponse {

    private String id;

    private String title;

    private Set<String> genres;

    private Integer durationMinutes;

    private Double rating;

    private String description;

    private String posterUrl;

    private LocalDate releaseDate;

    private List<String> languages;

    private List<String> cast;

    private String director;

    private Boolean active;
}

