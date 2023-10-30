package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmtpDto {
    private UUID id;
    @NotBlank(message = "cannot blank")
    private String username;
    @NotBlank(message = "cannot blank")
    private String password;
    private ApplicationDto applicationDto;
}
