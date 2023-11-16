package com.example;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@SpringBootApplication
@OpenAPIDefinition(
        servers = @Server(url = "/", description = "Default Server URL"),
        info = @Info(
                title = "schedule service",
                description = "Spring Boot API",
                version = "2.0.1"
        )
)
@SecurityScheme(
        name = "auth",
        type = SecuritySchemeType.OAUTH2,
        in = SecuritySchemeIn.HEADER,
        flows = @OAuthFlows(
                clientCredentials = @OAuthFlow(
                        tokenUrl = "https://keycloak-fintrack.sythorng.site/auth/realms/Fintrack/protocol/openid-connect/token"
                ),
                password = @OAuthFlow(
                        tokenUrl = "https://keycloak-fintrack.sythorng.site/auth/realms/Fintrack/protocol/openid-connect/token"
                )
        )
)
@Configuration("classpath:application-" + "${spring.profiles.active}" + ".properties")
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }
}