package com.example.repository;

import com.example.model.entities.UserSubscription;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Transactional
public interface WebRepository extends JpaRepository<UserSubscription, Long> {
    List<UserSubscription> findByUserId(String userId);

//    UserSubscription findByUserId(String id);
    void deleteByEndpointContains(String endpoint);

}
