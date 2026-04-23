package com.movieticket.mapper;

import com.movieticket.dto.response.RegisterResponse;
import com.movieticket.dto.response.UserResponse;
import com.movieticket.dto.response.UserUpdateResponse;
import com.movieticket.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserResponse toResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())  // String id
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public static RegisterResponse toRegisterResponse(User user) {
        return RegisterResponse.builder()
                .userId(user.getId()) // String id
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .message("User registered successfully")
                .build();
    }

    public static UserUpdateResponse toUpdateResponse(User user) {
        return UserUpdateResponse.builder()
                .id(user.getId()) // String id
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Profile updated successfully")
                .build();
    }
}
