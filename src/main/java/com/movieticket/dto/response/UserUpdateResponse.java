package com.movieticket.dto.response;


import com.movieticket.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateResponse {

    private String id;
    private String name;
    private String email;
    private Role role;
    private String message;
}
