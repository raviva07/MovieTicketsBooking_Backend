package com.movieticket.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
public class UserProfileResponse {

    private String id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private boolean enabled;
    private Set<String> roles;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;
}
