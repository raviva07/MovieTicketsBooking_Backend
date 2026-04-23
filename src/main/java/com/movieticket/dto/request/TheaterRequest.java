package com.movieticket.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TheaterRequest {

    @NotBlank(message = "Theater name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "Invalid phone number")
    private String phone;

    @Email(message = "Invalid contact email")
    private String contactEmail;

    /**
     * Safer structure instead of Object
     */
    private List<Map<String, String>> seatLayout;

    private Integer screens;

    private Map<String, String> metadata;
}
