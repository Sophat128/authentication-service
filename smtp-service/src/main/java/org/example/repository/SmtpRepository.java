package org.example.repository;

import org.example.entity.Smtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SmtpRepository extends JpaRepository<Smtp,UUID>{
    @Query(value = "SELECT * FROM smtp WHERE id = :id AND app_id = :appId", nativeQuery = true)
    Smtp findBySmtpById(UUID id, UUID appId);

}
