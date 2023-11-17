package org.example;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@SecurityScheme(
        name = "app",
        type = SecuritySchemeType.OAUTH2,
        in = SecuritySchemeIn.HEADER,
        flows = @OAuthFlows(
                clientCredentials = @OAuthFlow(
                        tokenUrl = "https://keycloak-fintrack.sythorng.site/auth/realms/Fintrack/protocol/openid-connect/token"

                ),
                password = @OAuthFlow(
                        tokenUrl = "https://keycloak-fintrack.sythorng.site/auth/realms/Fintrack/protocol/openid-connect/token"

                )
        ))

public class EmailKbServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmailKbServiceApplication.class, args);
    }
}