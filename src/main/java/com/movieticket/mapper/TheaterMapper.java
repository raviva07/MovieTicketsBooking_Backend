package com.movieticket.mapper;

import com.movieticket.dto.request.TheaterRequest;
import com.movieticket.dto.response.TheaterResponse;
import com.movieticket.entity.Theater;
import org.springframework.stereotype.Component;

@Component
public class TheaterMapper {

    public TheaterResponse toResponse(Theater theater) {
        if (theater == null) return null;

        return TheaterResponse.builder()
                .id(theater.getId())
                .name(theater.getName())
                .address(theater.getAddress())
                .city(theater.getCity())
                .state(theater.getState())
                .country(theater.getCountry())
                .phone(theater.getPhone())
                .contactEmail(theater.getContactEmail())
                .seatLayout(theater.getSeatLayout())
                .screens(theater.getScreens())
                .metadata(theater.getMetadata())
                .build();
    }

    public Theater toEntity(TheaterRequest req) {
        if (req == null) return null;

        return Theater.builder()
                .name(req.getName())
                .address(req.getAddress())
                .city(req.getCity())
                .state(req.getState())
                .country(req.getCountry())
                .phone(req.getPhone())
                .contactEmail(req.getContactEmail())
                .seatLayout(req.getSeatLayout())
                .screens(req.getScreens())
                .metadata(req.getMetadata())
                .build();
    }

    public void updateEntity(Theater theater, TheaterRequest req) {
        if (theater == null || req == null) return;

        if (req.getName() != null) theater.setName(req.getName());
        if (req.getAddress() != null) theater.setAddress(req.getAddress());
        if (req.getCity() != null) theater.setCity(req.getCity());
        if (req.getState() != null) theater.setState(req.getState());
        if (req.getCountry() != null) theater.setCountry(req.getCountry());
        if (req.getPhone() != null) theater.setPhone(req.getPhone());
        if (req.getContactEmail() != null) theater.setContactEmail(req.getContactEmail());
        if (req.getSeatLayout() != null) theater.setSeatLayout(req.getSeatLayout());
        if (req.getScreens() != null) theater.setScreens(req.getScreens());
        if (req.getMetadata() != null) theater.setMetadata(req.getMetadata());
    }
}
