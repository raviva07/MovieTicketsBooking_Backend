package com.movieticket.repository;

import com.movieticket.entity.User;
import com.movieticket.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // 🔐 Authentication
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // 👑 Admin operations
    List<User> findByRole(Role role);

    long countByRole(Role role);
}
