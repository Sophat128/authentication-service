package org.example.model;

import com.example.constant.PlatformType;
import com.example.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.dto.ApplicationDto;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import java.util.Collection;
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
    @ElementCollection
    @JoinTable(name = "tblPlatformTypes", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "platformType", nullable = false)
    @Enumerated(EnumType.STRING)
    private Collection<PlatformType> platformType;
    @Column(name = "user_id",nullable = false)
    private UUID userId;
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private LocalDateTime createdDate;


    public ApplicationDto toDto(UserDto userDto){
        return new ApplicationDto(this.id,this.name,this.platformType,this.createdDate,userDto);
    }

}
