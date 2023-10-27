package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmtpDto {
    private UUID id;
    private String username;
    private String password;
    private ApplicationDto applicationDto;
}
