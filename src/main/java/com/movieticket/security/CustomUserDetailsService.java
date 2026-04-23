package com.movieticket.security;


import com.movieticket.entity.User;
import com.movieticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;


import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = user.getRole().name();

        // SAFE: ensure no duplicate ROLE_
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(role));

        return new CustomUserDetails(user, authorities);
    }
}
