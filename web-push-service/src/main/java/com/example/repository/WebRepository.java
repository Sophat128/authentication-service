package com.example.repository;

import com.example.model.entities.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebRepository extends JpaRepository<UserSubscription, Long> {
    UserSubscription findByUserId(Long id);
    void deleteByEndpoint(String endpoint);
}
