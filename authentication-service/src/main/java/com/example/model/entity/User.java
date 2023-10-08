package com.example.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.microservice.fintrack.dto.UserDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;

    public static UserDto toDto(UserRepresentation userRepresentation) {
        return new UserDto(
                UUID.fromString(userRepresentation.getId()),
                userRepresentation.getUsername(),
                userRepresentation.getEmail(),
                LocalDateTime.parse(userRepresentation.getAttributes().get("createdDate").get(0)),
                LocalDateTime.parse(userRepresentation.getAttributes().get("lastModified").get(0))
        );
    }
    public static UserRepresentation toUserRepresentation(UserDto userDto) {
        UserRepresentation userRepresentation =new UserRepresentation();
        userRepresentation.setUsername(userDto.getUsername());
        userRepresentation.setId(String.valueOf(userDto.getId()));
        userRepresentation.setEmail(userDto.getEmail());
        userRepresentation.singleAttribute("createdDate", String.valueOf(userDto.getCreatedDate()));
        userRepresentation.singleAttribute("lastModified", String.valueOf(userDto.getLastModified()));
        return userRepresentation;
    }
}
