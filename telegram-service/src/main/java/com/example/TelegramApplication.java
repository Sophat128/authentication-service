package com.example;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(
        name = "auth",
        type = SecuritySchemeType.OAUTH2,
        in = SecuritySchemeIn.HEADER,
        flows = @OAuthFlows(
                clientCredentials = @OAuthFlow(
                        tokenUrl = "http://localhost:8443/auth/realms/client_user/protocol/openid-connect/token"
                ),
                password = @OAuthFlow(
                        tokenUrl = "http://localhost:8443/auth/realms/client_user/protocol/openid-connect/token"
                )
        )
)
public class TelegramApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramApplication.class, args);
    }
}