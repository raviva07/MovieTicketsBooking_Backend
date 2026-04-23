package com.movieticket.controller;
import com.movieticket.dto.request.AuthRequest;
import com.movieticket.dto.request.RegisterRequest;
import com.movieticket.dto.response.AuthResponse;
import com.movieticket.service.AuthService;
import com.movieticket.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.movieticket.dto.response.ApiResponse;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message(Constants.USER_REGISTERED)
                .data(authService.register(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message(Constants.USER_LOGIN_SUCCESS)
                .data(authService.login(request))
                .build();
    }
}
