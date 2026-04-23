package com.movieticket.mapper;

import com.movieticket.dto.request.ShowRequest;
import com.movieticket.dto.response.ShowResponse;
import com.movieticket.entity.Show;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ShowMapper {

    public ShowResponse toResponse(Show show) {
        if (show == null) return null;

        return ShowResponse.builder()
                .id(show.getId())
                .movieId(show.getMovieId())
                .theaterId(show.getTheaterId())
                .startTime(show.getStartTime())
                .endTime(show.getEndTime())
                .seatStatusMap(show.getSeatStatusMap() == null ? null :
                        show.getSeatStatusMap().entrySet().stream()
                                .collect(Collectors.toMap(
                                        e -> e.getKey(),
                                        e -> e.getValue().name()
                                )))
                .seatPricing(show.getSeatPricing())
                .defaultPrice(show.getDefaultPrice())
                .availableSeats(show.getAvailableSeats())
                .screen(show.getScreen())
                .language(show.getLanguage())
                .active(show.isActive())
                .metadata(show.getMetadata())
                .build();
    }

    public Show toEntity(ShowRequest req) {
        if (req == null) return null;

        return Show.builder()
                .movieId(req.getMovieId())
                .theaterId(req.getTheaterId())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .seatPricing(req.getSeatPricing())
                .defaultPrice(req.getDefaultPrice())
                .screen(req.getScreen())
                .language(req.getLanguage())
                .active(req.getActive() != null ? req.getActive() : true)
                .build();
    }

    public void updateEntity(Show show, ShowRequest req) {
        if (show == null || req == null) return;

        if (req.getMovieId() != null) show.setMovieId(req.getMovieId());
        if (req.getTheaterId() != null) show.setTheaterId(req.getTheaterId());
        if (req.getStartTime() != null) show.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) show.setEndTime(req.getEndTime());
        if (req.getSeatPricing() != null) show.setSeatPricing(req.getSeatPricing());
        if (req.getDefaultPrice() != null) show.setDefaultPrice(req.getDefaultPrice());
        if (req.getScreen() != null) show.setScreen(req.getScreen());
        if (req.getLanguage() != null) show.setLanguage(req.getLanguage());
        if (req.getActive() != null) show.setActive(req.getActive());
    }
}
