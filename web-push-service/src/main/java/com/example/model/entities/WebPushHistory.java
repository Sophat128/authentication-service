package com.example.model.entities;

import com.example.model.type.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)

public class WebPushHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String MessageName;
    private String notificationType;
    private String status;
    @CreatedDate
    private Date date;
}
