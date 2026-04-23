package com.movieticket.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class TheaterResponse {

    private String id;

    private String name;
    private String address;

    private String city;
    private String state;
    private String country;

    private String phone;
    private String contactEmail;

    /**
     * Fixed unsafe Object → String
     */
    private List<Map<String, String>> seatLayout;

    private Integer screens;

    private Map<String, String> metadata;
}
