package com.movieticket.service;


import com.movieticket.entity.User;

import com.movieticket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserByEmail() {
        User user = User.builder()
                .email("test@mail.com")
                .build();

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("test@mail.com");

        assertNotNull(result);
        assertEquals("test@mail.com", result.getEmail());
    }

    // 🔥 Negative case
    @Test
    void testGetUserByEmail_NotFound() {
        when(userRepository.findByEmail("wrong@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.getUserByEmail("wrong@mail.com"));
    }
}
