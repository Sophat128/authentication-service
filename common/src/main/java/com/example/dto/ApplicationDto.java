package com.example.dto;

import com.example.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {
    private UUID id ;
    private String name;
    private String platformType;
    private LocalDateTime createdDate;
    private UserDto userDto;
}
