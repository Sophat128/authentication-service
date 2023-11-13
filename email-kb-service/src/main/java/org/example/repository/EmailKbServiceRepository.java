package org.example.repository;

import org.example.model.Smtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailKbServiceRepository extends JpaRepository<Smtp, Long> {
    @Query(value = "SELECT * FROM smtp_kb WHERE id = :id AND user_id = :userId", nativeQuery = true)
    Smtp findIdandUserId(Long id, UUID userId);
}
