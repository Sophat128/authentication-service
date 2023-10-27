package org.example.entity;

import com.example.dto.ApplicationDto;
import com.example.dto.SmtpDto;
import com.example.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Smtp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "username", length = 100, nullable = false)
    private String username;
    @Column(name = "password", length = 100, nullable = false)
    private String password;
    private UUID appId;


    public SmtpDto toDto(ApplicationDto applicationDto){
        return new SmtpDto(this.id,this.username,this.password,applicationDto);
    }

}
