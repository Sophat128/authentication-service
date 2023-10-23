package org.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microservice.fintrack.dto.UserDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {
    private UUID id ;
    private String name;
    private String platformType;
    private LocalDateTime createdDate;
    private UserDto userId;
}
