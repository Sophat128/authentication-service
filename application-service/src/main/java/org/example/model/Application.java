package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id ;
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    @Column(name = "platform",nullable = false)
    private String platformType;
    private UUID userId;
    private LocalDateTime createdDate;

}
