package com.example.repository;

import com.example.entities.request.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebRepository extends JpaRepository<UserSubscription, Long> {
    UserSubscription findByUserId(Long id);
}
