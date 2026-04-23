package com.movieticket.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "theaters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Theater implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String country;

    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$")
    private String phone;

    @Email
    private String contactEmail;

    /**
     * [longitude, latitude]
     */
    @GeoSpatialIndexed
    private double[] location;

    /**
     * Seat layout (initial template)
     */
    private List<Map<String, String>> seatLayout;

    private Integer screens;

    private Map<String, String> metadata;

    private boolean active = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
