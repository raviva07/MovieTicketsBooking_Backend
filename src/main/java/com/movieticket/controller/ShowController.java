package com.movieticket.controller;

import com.movieticket.dto.request.ShowRequest;
import com.movieticket.dto.response.ApiResponse;
import com.movieticket.dto.response.ShowResponse;
import com.movieticket.service.ShowService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    // ================= GET ALL =================
    @GetMapping
    public ApiResponse<List<ShowResponse>> getAll() {
        return ApiResponse.<List<ShowResponse>>builder()
                .success(true)
                .message("Shows fetched successfully") // 🔥 important (plural)
                .data(showService.getAll())
                .build();
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ApiResponse<ShowResponse> getById(@PathVariable String id) {
        return ApiResponse.<ShowResponse>builder()
                .success(true)
                .message("Show fetched successfully")
                .data(showService.getById(id))
                .build();
    }

    // ================= CREATE =================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ShowResponse> create(@RequestBody ShowRequest req) {
        return ApiResponse.<ShowResponse>builder()
                .success(true)
                .message("Show created successfully")
                .data(showService.create(req))
                .build();
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ShowResponse> update(
            @PathVariable String id,
            @RequestBody ShowRequest req
    ) {
        return ApiResponse.<ShowResponse>builder()
                .success(true)
                .message("Show updated successfully")
                .data(showService.update(id, req))
                .build();
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> delete(@PathVariable String id) {
        showService.delete(id);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Show deleted successfully")
                .data("SUCCESS")
                .build();
    }
    
    @GetMapping("/theater/{theaterId}")
    public ApiResponse<List<ShowResponse>> getByTheater(@PathVariable String theaterId) {
        return ApiResponse.<List<ShowResponse>>builder()
                .success(true)
                .message("Shows fetched successfully")
                .data(showService.getByTheater(theaterId))
                .build();
    }

}
