package com.movieticket.controller;

import com.movieticket.dto.request.UserUpdateRequest;
import com.movieticket.dto.response.ApiResponse;
import com.movieticket.dto.response.UserResponse;
import com.movieticket.dto.response.UserUpdateResponse;
import com.movieticket.entity.User;
import com.movieticket.mapper.UserMapper;
import com.movieticket.service.UserService;
import com.movieticket.util.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // =========================
    // USER + ADMIN → PROFILE (FIXED)
    // =========================
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ApiResponse<UserResponse> getProfile(Authentication authentication) {

        User user = userService.getUserByEmail(authentication.getName());

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message(Constants.USER_FETCHED)
                .data(UserMapper.toResponse(user))
                .build();
    }

    // =========================
    // USER + ADMIN → UPDATE PROFILE (FIXED)
    // =========================
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ApiResponse<UserUpdateResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest request) {

        User user = userService.getUserByEmail(authentication.getName());

        User updatedUser = userService.updateProfile(user, request);

        return ApiResponse.<UserUpdateResponse>builder()
                .success(true)
                .message(Constants.USER_UPDATED)
                .data(UserMapper.toUpdateResponse(updatedUser))
                .build();
    }

    // =========================
    // ADMIN ONLY → GET ALL USERS
    // =========================
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserResponse>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(UserMapper::toResponse)
                .toList();

        return ApiResponse.<List<UserResponse>>builder()
                .success(true)
                .message(Constants.USER_FETCHED)
                .data(users)
                .build();
    }

    // =========================
    // ADMIN ONLY → DELETE USER
    // =========================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteUser(@PathVariable String id) {

        userService.deleteUser(id);

        return ApiResponse.<String>builder()
                .success(true)
                .message(Constants.USER_DELETED)
                .data("User deleted successfully")
                .build();
    }
    
 // =========================
 // ADMIN → UPDATE USER BY ID
 // =========================
 @PutMapping("/{id}")
 @PreAuthorize("hasRole('ADMIN')")
 public ApiResponse<UserResponse> updateUserByAdmin(
         @PathVariable String id,
         @Valid @RequestBody UserUpdateRequest request) {

     User updatedUser = userService.updateUserByAdmin(id, request);

     return ApiResponse.<UserResponse>builder()
             .success(true)
             .message("User updated successfully")
             .data(UserMapper.toResponse(updatedUser))
             .build();
 }

}
