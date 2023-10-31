package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScheduleTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "notification_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date notificationDatetime;

    @Column(name = "notification_content", columnDefinition = "TEXT")
    private String notificationContent;

    @Column(name = "notification_type", length = 50)
    private String notificationType;

    @Column(name = "status", length = 50)
    private String status;
}
