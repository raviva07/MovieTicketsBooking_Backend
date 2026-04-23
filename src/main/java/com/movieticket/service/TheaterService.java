package com.movieticket.service;

import com.movieticket.dto.request.TheaterRequest;
import com.movieticket.dto.response.TheaterResponse;
import com.movieticket.entity.Seat;
import com.movieticket.entity.SeatStatus;
import com.movieticket.entity.SeatType;
import com.movieticket.entity.Theater;
import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.mapper.TheaterMapper;
import com.movieticket.repository.SeatRepository;
import com.movieticket.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheaterService {

    private final TheaterRepository theaterRepository;
    private final TheaterMapper theaterMapper;
    private final SeatRepository seatRepository;

    // ================= CREATE =================
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public TheaterResponse create(TheaterRequest req) {
        Theater saved = theaterRepository.save(theaterMapper.toEntity(req));

        // Generate seats if seatLayout is provided
        if (req.getSeatLayout() != null && !req.getSeatLayout().isEmpty()) {
            List<Seat> seats = new ArrayList<>();
            for (Map<String, String> rowDef : req.getSeatLayout()) {
                String row = rowDef.get("row");
                int count = Integer.parseInt(rowDef.getOrDefault("count", "0"));
                String type = rowDef.getOrDefault("type", "REGULAR");
                BigDecimal basePrice = new BigDecimal(rowDef.getOrDefault("basePrice", "200"));

                for (int pos = 1; pos <= count; pos++) {
                    Seat seat = Seat.builder()
                            .theaterId(saved.getId())
                            .row(row)
                            .position(pos)
                            .seatNumber(row + pos)
                            .type(SeatType.valueOf(type.toUpperCase()))
                            .basePrice(basePrice)
                            .status(SeatStatus.AVAILABLE)
                            .build();
                    seats.add(seat);
                }
            }
            seatRepository.saveAll(seats);
            log.info("Generated {} seats for theater {}", seats.size(), saved.getName());
        }

        return theaterMapper.toResponse(saved);
    }

    // ================= UPDATE =================
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public TheaterResponse update(String id, TheaterRequest req) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found: " + id));

        theaterMapper.updateEntity(theater, req);
        Theater updated = theaterRepository.save(theater);

        // Optional: regenerate seats if seatLayout changed
        if (req.getSeatLayout() != null && !req.getSeatLayout().isEmpty()) {
            // Delete old seats
            seatRepository.findByTheaterId(updated.getId())
                    .forEach(seat -> seatRepository.deleteById(seat.getId()));

            // Generate new seats
            List<Seat> seats = new ArrayList<>();
            for (Map<String, String> rowDef : req.getSeatLayout()) {
                String row = rowDef.get("row");
                int count = Integer.parseInt(rowDef.getOrDefault("count", "0"));
                String type = rowDef.getOrDefault("type", "REGULAR");
                BigDecimal basePrice = new BigDecimal(rowDef.getOrDefault("basePrice", "200"));

                for (int pos = 1; pos <= count; pos++) {
                    Seat seat = Seat.builder()
                            .theaterId(updated.getId())
                            .row(row)
                            .position(pos)
                            .seatNumber(row + pos)
                            .type(SeatType.valueOf(type.toUpperCase()))
                            .basePrice(basePrice)
                            .status(SeatStatus.AVAILABLE)
                            .build();
                    seats.add(seat);
                }
            }
            seatRepository.saveAll(seats);
            log.info("Regenerated {} seats for theater {}", seats.size(), updated.getName());
        }

        return theaterMapper.toResponse(updated);
    }

    // ================= DELETE =================
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String id) {
        if (!theaterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Theater not found: " + id);
        }
        // Delete seats too
        seatRepository.findByTheaterId(id)
                .forEach(seat -> seatRepository.deleteById(seat.getId()));

        theaterRepository.deleteById(id);
        log.info("Deleted theater {} and its seats", id);
    }

    // ================= GET BY ID =================
    public TheaterResponse getById(String id) {
        return theaterMapper.toResponse(
                theaterRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Theater not found: " + id))
        );
    }

    // ================= GET ALL =================
    public List<TheaterResponse> getAll() {
        return theaterRepository.findAll()
                .stream()
                .map(theaterMapper::toResponse)
                .collect(Collectors.toList());
    }
}
