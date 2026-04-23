package com.movieticket.controller;

import com.movieticket.dto.request.TheaterRequest;
import com.movieticket.dto.response.ApiResponse;
import com.movieticket.dto.response.TheaterResponse;
import com.movieticket.service.TheaterService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    // ================= GET ALL =================
    @GetMapping
    public ApiResponse<List<TheaterResponse>> getAll() {
        return ApiResponse.<List<TheaterResponse>>builder()
                .success(true)
                .message("Theaters fetched successfully") // 🔥 plural fix
                .data(theaterService.getAll())
                .build();
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ApiResponse<TheaterResponse> getById(@PathVariable String id) {
        return ApiResponse.<TheaterResponse>builder()
                .success(true)
                .message("Theater fetched successfully")
                .data(theaterService.getById(id))
                .build();
    }

    // ================= CREATE =================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TheaterResponse> create(@RequestBody TheaterRequest req) {
        return ApiResponse.<TheaterResponse>builder()
                .success(true)
                .message("Theater created successfully")
                .data(theaterService.create(req))
                .build();
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TheaterResponse> update(
            @PathVariable String id,
            @RequestBody TheaterRequest req
    ) {
        return ApiResponse.<TheaterResponse>builder()
                .success(true)
                .message("Theater updated successfully")
                .data(theaterService.update(id, req))
                .build();
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> delete(@PathVariable String id) {
        theaterService.delete(id);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Theater deleted successfully")
                .data("SUCCESS")
                .build();
    }
}

