package com.movieticket.repository;

import com.movieticket.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    Page<Notification> findByUserId(String userId, Pageable pageable);

    List<Notification> findByUserIdAndReadFalse(String userId);
    List<Notification> findByUserId(String userId);


    long countByUserIdAndReadFalse(String userId);
}
