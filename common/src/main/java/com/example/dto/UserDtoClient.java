package com.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoClient {
    private UUID id;
    private String username;
    private String email;
    private String profile;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;

//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private String notificationType;

//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private String balance;

}