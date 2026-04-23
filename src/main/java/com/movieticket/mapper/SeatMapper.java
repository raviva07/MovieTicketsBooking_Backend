package com.movieticket.mapper;

import com.movieticket.dto.response.SeatResponse;
import com.movieticket.entity.Seat;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

	public SeatResponse toResponse(Seat seat) {
	    if (seat == null) return null;

	    return SeatResponse.builder()
	            .id(seat.getId())
	            .theaterId(seat.getTheaterId())
	            .seatNumber(seat.getSeatNumber())
	            .row(seat.getRow())
	            .position(seat.getPosition())
	            .type(seat.getType() != null ? seat.getType().name() : null)
	            .basePrice(seat.getBasePrice())
	            .status(seat.getStatus() != null ? seat.getStatus().name() : null)
	            .metadata(seat.getMetadata())
	            .build();
	}

}
