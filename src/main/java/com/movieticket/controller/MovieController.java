package com.movieticket.controller;

import com.movieticket.dto.request.MovieRequest;
import com.movieticket.dto.response.ApiResponse;
import com.movieticket.dto.response.MovieResponse;
import com.movieticket.service.MovieService;
import com.movieticket.util.Constants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // ================= GET ALL =================
    @GetMapping
    public ApiResponse<List<MovieResponse>> getAll() {
        return ApiResponse.<List<MovieResponse>>builder()
                .success(true)
                .message(Constants.MOVIE_FETCHED)
                .data(movieService.getAll())
                .build();
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ApiResponse<MovieResponse> getById(@PathVariable String id) {
        return ApiResponse.<MovieResponse>builder()
                .success(true)
                .message(Constants.MOVIE_FETCHED)
                .data(movieService.getById(id))
                .build();
    }

    // ================= CREATE (MULTIPART) =================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = {"multipart/form-data"})
    public ApiResponse<MovieResponse> createMovieMultipart(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String posterUrl,
            @RequestParam(required = false) MultipartFile posterFile
    ) throws IOException {
        MovieRequest req = new MovieRequest();
        req.setTitle(title);
        req.setDescription(description);
        req.setDirector(director);
        req.setPosterUrl(posterUrl);
        req.setPosterFile(posterFile);

        MovieResponse response = movieService.create(req);

        return ApiResponse.<MovieResponse>builder()
                .success(true)
                .message(Constants.MOVIE_CREATED)
                .data(response)
                .build();
    }

    // ================= CREATE (JSON) =================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = {"application/json"})
    public ApiResponse<MovieResponse> createMovieJson(@Valid @RequestBody MovieRequest req) {
        MovieResponse response = movieService.create(req);
        return ApiResponse.<MovieResponse>builder()
                .success(true)
                .message(Constants.MOVIE_CREATED)
                .data(response)
                .build();
    }

    // ================= UPDATE =================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<MovieResponse> updateMovie(
            @PathVariable String id,
            @Valid @RequestBody MovieRequest req
    ) {
        MovieResponse response = movieService.update(id, req);
        return ApiResponse.<MovieResponse>builder()
                .success(true)
                .message(Constants.MOVIE_UPDATED)
                .data(response)
                .build();
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteMovie(@PathVariable String id) {
        movieService.delete(id);
        return ApiResponse.<String>builder()
                .success(true)
                .message(Constants.MOVIE_DELETED)
                .data("SUCCESS")
                .build();
    }

    // ================= UPLOAD POSTER (FILE) =================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload-poster")
    public ApiResponse<String> uploadPoster(@RequestParam MultipartFile file) throws IOException {
        String url = movieService.uploadPoster(file);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Poster uploaded to Cloudinary")
                .data(url)
                .build();
    }

    // ================= UPLOAD POSTER (URL) =================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload-poster-url")
    public ApiResponse<String> uploadPosterFromUrl(@RequestParam String url) throws IOException {
        String cloudUrl = movieService.uploadPosterFromUrl(url);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Poster uploaded from URL to Cloudinary")
                .data(cloudUrl)
                .build();
    }
}
