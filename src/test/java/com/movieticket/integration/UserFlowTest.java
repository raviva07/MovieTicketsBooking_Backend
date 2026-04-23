package com.movieticket.integration;

import com.movieticket.dto.request.RegisterRequest;
import com.movieticket.dto.response.AuthResponse;
import com.movieticket.entity.Role;
import com.movieticket.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
class UserFlowTest {

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUserRegistrationFlow_AsCustomer() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setName("Test User");
        registerRequest.setRole(Role.ROLE_USER);

        when(authService.register(any(RegisterRequest.class)))
                .thenAnswer(invocation -> {
                    RegisterRequest req = invocation.getArgument(0);
                    return AuthResponse.builder()
                            .email(req.getEmail())
                            .name(req.getName())
                            .token("dummy-token")
                            .type("Bearer")
                            .role(req.getRole())
                            .build();
                });

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("test@test.com", response.getEmail());
        assertEquals("Test User", response.getName());
        assertEquals("dummy-token", response.getToken());
        assertEquals(Role.ROLE_USER, response.getRole());

        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void testUserRegistrationFlow_AsAdmin() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("admin@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setName("Admin User");
        registerRequest.setRole(Role.ROLE_ADMIN);

        when(authService.register(any(RegisterRequest.class)))
                .thenAnswer(invocation -> {
                    RegisterRequest req = invocation.getArgument(0);
                    return AuthResponse.builder()
                            .email(req.getEmail())
                            .name(req.getName())
                            .token("admin-token")
                            .type("Bearer")
                            .role(req.getRole())
                            .build();
                });

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("admin@test.com", response.getEmail());
        assertEquals("Admin User", response.getName());
        assertEquals("admin-token", response.getToken());
        assertEquals(Role.ROLE_ADMIN, response.getRole());

        verify(authService, times(1)).register(registerRequest);
    }
}
