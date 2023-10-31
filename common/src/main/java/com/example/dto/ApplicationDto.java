package com.example.dto;

import com.example.constant.PlatformType;
import com.example.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {
    private UUID id ;
    private String name;
    private Collection<PlatformType> platformType;
    private LocalDateTime createdDate;
    private UserDto userDto;
}
