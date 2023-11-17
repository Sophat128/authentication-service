package com.example.models.entity;

import com.example.models.dto.MailScheduleDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

//import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Entity
@ToString
@Table(name = "mail_schedule")
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class MailSchedule implements Serializable {

    private static final long serialVersionUID = 6321323265487281667L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "to_email")
    private String toEmail;

    @Column(name = "message")
    private String message;

    @Column(name = "schedule_datetime")
    private String scheduleDateTime;

    @Column(name = "schedule_zone_id")
    private String scheduleZoneId;

    @Column(name = "is_sended")
    private boolean isSended;

    public MailScheduleDto toDto() {
        MailScheduleDto response = new MailScheduleDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        response.setScheduleId(this.scheduleId);
        response.setUserId(UUID.fromString(this.userId));
        response.setMessage(this.message);
        response.setScheduledTime(LocalDateTime.parse(this.scheduleDateTime, formatter));
        response.setZoneId(ZoneId.of(this.scheduleZoneId));
        return response;
    }

    public MailSchedule(Long scheduleId, String userId, String message, String scheduleDateTime, String scheduleZoneId) {
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.message = message;
        this.scheduleDateTime = scheduleDateTime;
        this.scheduleZoneId = scheduleZoneId;
    }
}
