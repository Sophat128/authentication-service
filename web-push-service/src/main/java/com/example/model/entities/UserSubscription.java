package com.example.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String endpoint;
    private String auth;
    private String p256dh;

    private String userId;

    public UserSubscription(String endpoint, String auth, String p256dh, String userId) {
        this.endpoint = endpoint;
        this.auth = auth;
        this.p256dh = p256dh;
        this.userId = userId;
    }

    // Other subscription fields, getters, and setters
}
