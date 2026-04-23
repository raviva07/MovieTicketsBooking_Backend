package com.movieticket.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

@Data
public class MovieRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 5, message = "Maximum 5 genres allowed")
    private Set<@NotBlank String> genres;

    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    private Double rating;

    @Size(max = 2000)
    private String description;

    @Size(max = 2048)
    private String posterUrl;

    private LocalDate releaseDate;

    private List<String> languages;

    private List<String> cast;

    private String director;
    
    private MultipartFile posterFile;

    private Boolean active = true;
}
