package com.movieticket.service;

import com.movieticket.dto.request.ShowRequest;
import com.movieticket.dto.response.ShowResponse;
import com.movieticket.entity.Show;
import com.movieticket.exception.BadRequestException;
import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.mapper.ShowMapper;
import com.movieticket.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowService {

    private final ShowRepository showRepository;
    private final ShowMapper showMapper;

    public ShowResponse create(ShowRequest req) {
        if (req.getStartTime().isAfter(req.getEndTime())) {
            throw new BadRequestException("Start time must be before end time");
        }
        Show saved = showRepository.save(showMapper.toEntity(req));
        return showMapper.toResponse(saved);
    }

    public ShowResponse update(String id, ShowRequest req) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + id));
        showMapper.updateEntity(show, req);
        return showMapper.toResponse(showRepository.save(show));
    }

    public void delete(String id) {
        if (!showRepository.existsById(id)) {
            throw new ResourceNotFoundException("Show not found: " + id);
        }
        showRepository.deleteById(id);
    }

    public ShowResponse getById(String id) {
        return showMapper.toResponse(
                showRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + id))
        );
    }

    public List<ShowResponse> getAll() {
        return showRepository.findAll()
                .stream()
                .map(showMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<ShowResponse> getByTheater(String theaterId) {
        return showRepository.findByTheaterId(theaterId)
                .stream()
                .map(showMapper::toResponse)
                .toList();
    }

}
