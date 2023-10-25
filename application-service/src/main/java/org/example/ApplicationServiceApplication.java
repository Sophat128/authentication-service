package org.example;


import com.example.dto.UserDto;
import org.example.model.PlatformType;
import org.example.model.request.ApplicationRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.jwt.Jwt;

@SpringBootApplication
public class ApplicationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationServiceApplication.class, args);
    }

  
}