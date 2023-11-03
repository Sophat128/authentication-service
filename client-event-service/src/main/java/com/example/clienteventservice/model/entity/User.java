package com.example.clienteventservice.model.entity;


import com.example.dto.UserDtoClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private UUID id;
    private String username;
    private String email;
    private String profile;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;

//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private String notificationType;

//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private BigDecimal balance;


    public static UserDtoClient toDto(UserRepresentation userRepresentation, String url) {
        return new UserDtoClient(
                UUID.fromString(userRepresentation.getId()),
                userRepresentation.getUsername(),
                userRepresentation.getEmail(),
                url+userRepresentation.getAttributes().get("profile").get(0),
                LocalDateTime.parse(userRepresentation.getAttributes().get("createdDate").get(0)),
                LocalDateTime.parse(userRepresentation.getAttributes().get("lastModified").get(0))
//                userRepresentation.getAttributes().get("notificationType").get(0),
//                userRepresentation.getAttributes().get("balance").get(0)
        );
    }
}

