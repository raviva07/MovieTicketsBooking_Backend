package com.movieticket.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Document(collection = "movies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie implements Serializable {

    @Id
    private String id;

    @NotBlank
    @Indexed
    private String title;

    @Size(max = 5)
    private Set<@NotBlank String> genres;

    @NotNull
    @Positive
    private Integer durationMinutes;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private BigDecimal rating;

    @Size(max = 2000)
    private String description;

    @NotBlank
    private String posterUrl;

    private LocalDate releaseDate;

    @NotEmpty
    private List<@NotBlank String> languages;

    private List<String> cast;

    private String director;

    private Boolean active = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

