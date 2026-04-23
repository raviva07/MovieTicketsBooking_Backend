package com.movieticket.controller;


import com.movieticket.config.TestSecurityConfig;

import com.movieticket.entity.User;
import com.movieticket.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;



import java.util.List;

  // <-- important import
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest {

    @MockBean
    private UserService userService;

    @Test
    void testGetAllUsers() throws Exception {
        User user = new User();
        user.setId("1");
        user.setName("Test User");
        user.setEmail("test@mail.com");

        Mockito.when(userService.getAllUsers())
                .thenReturn(List.of(user));

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].email").value("test@mail.com"));
    }
}
