package org.example.repository;

import org.example.model.Application;
import org.example.model.PlatformType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByUserId(UUID userId);
    Application findApplicationByIdAndUserId(UUID id, UUID userId);
    @Query(value = "SELECT * FROM application WHERE platform = :platform AND user_id = :userId", nativeQuery = true)
    List<Application> findByPlatformAndUserId(@Param("platform")String platformType,@Param("userId") UUID userId);
}
