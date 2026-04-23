package com.movieticket.service;

import com.movieticket.dto.request.MovieRequest;
import com.movieticket.dto.response.MovieResponse;
import com.movieticket.entity.Movie;
import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.integration.CloudinaryService;
import com.movieticket.mapper.MovieMapper;
import com.movieticket.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final CloudinaryService cloudinaryService; // already injected

    // ================= CREATE =================
    public MovieResponse create(MovieRequest req) {
        Movie movie = movieMapper.toEntity(req);

        // ✅ Handle poster upload
        try {
            if (req.getPosterFile() != null && !req.getPosterFile().isEmpty()) {
                movie.setPosterUrl(uploadPoster(req.getPosterFile()));
            } else if (req.getPosterUrl() != null && !req.getPosterUrl().isBlank()) {
                movie.setPosterUrl(uploadPosterFromUrl(req.getPosterUrl()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Poster upload failed", e);
        }

        return movieMapper.toResponse(movieRepository.save(movie));
    }

    // ================= UPDATE =================
    public MovieResponse update(String id, MovieRequest req) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + id));

        movieMapper.updateEntity(movie, req);

        // ✅ Handle poster upload on update
        try {
            if (req.getPosterFile() != null && !req.getPosterFile().isEmpty()) {
                movie.setPosterUrl(uploadPoster(req.getPosterFile()));
            } else if (req.getPosterUrl() != null && !req.getPosterUrl().isBlank()) {
                movie.setPosterUrl(uploadPosterFromUrl(req.getPosterUrl()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Poster upload failed", e);
        }

        return movieMapper.toResponse(movieRepository.save(movie));
    }

    // ================= DELETE =================
    public void delete(String id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movie not found: " + id);
        }
        movieRepository.deleteById(id);
    }

    // ================= GET =================
    public MovieResponse getById(String id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + id));
        return movieMapper.toResponse(movie);
    }

    public List<MovieResponse> getAll() {
        return movieRepository.findAll()
                .stream()
                .map(movieMapper::toResponse)
                .toList();
    }

    // ================= UPLOAD =================
    public String uploadPoster(MultipartFile file) throws IOException {
        return cloudinaryService.uploadFile(file);
    }

    public String uploadPosterFromUrl(String url) throws IOException {
        return cloudinaryService.uploadFromUrl(url);
    }
}

