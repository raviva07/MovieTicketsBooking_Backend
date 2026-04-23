package com.movieticket.repository;

import com.movieticket.entity.Role;
import com.movieticket.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
@DataMongoTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = User.builder()
                .id("1")
                .name("John Doe")
                .email("john@mail.com")
                .password("secret")
                .role(Role.ROLE_USER)
                .build();
        user = userRepository.save(user);
    }

    @Test
    void testFindByEmail() {
        Optional<User> found = userRepository.findByEmail("john@mail.com");
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
    }

    @Test
    void testSaveAndFindById() {
        Optional<User> found = userRepository.findById(user.getId());
        assertTrue(found.isPresent());
        assertEquals("john@mail.com", found.get().getEmail());
    }

    @Test
    void testDeleteUser() {
        userRepository.delete(user);
        Optional<User> found = userRepository.findById(user.getId());
        assertFalse(found.isPresent());
    }
}

