package org.example.service;

import com.example.dto.ApplicationDto;
import com.example.dto.SmtpDto;
import com.example.response.ApiResponse;
import org.example.entity.Smtp;
import org.example.entity.request.SmtpRequest;
import org.example.exception.ForbiddenException;
import org.example.repository.SmtpRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.Principal;
import java.util.UUID;

@Service
public class SmtpServiceImpl implements SmtpService{
    private final SmtpRepository smtpRepository;
    private final WebClient webClient;

    public SmtpServiceImpl(SmtpRepository smtpRepository, WebClient webClient) {
        this.smtpRepository = smtpRepository;
        this.webClient = webClient;
    }
    public ApplicationDto getAppById(UUID appId,Jwt jwt) {
        return webClient.get()
                .uri("/application/{appId}", appId)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(jwt.getTokenValue()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ApplicationDto>>() {})
                .block().getPayload();

    }

    @Override
    public SmtpDto setUpSmtp(SmtpRequest smtpRequest, Principal principal, Jwt jwt, UUID appId) {
        if (principal.getName()==null){
            throw new ForbiddenException("need token");
        }
        UUID userId = UUID.fromString(principal.getName());
        Smtp smtp = new Smtp();
        smtp.setUsername(smtpRequest.getUsername());
        smtp.setPassword(smtpRequest.getPassword());
        smtp.setAppId(appId);
        smtpRepository.save(smtp);
        SmtpDto smtpDto = new SmtpDto();
        smtpDto.setId(smtp.getId());
        smtpDto.setUsername(smtp.getUsername());
        smtpDto.setPassword(smtp.getPassword());
        ApplicationDto appById =getAppById(smtp.getAppId(),jwt);
        return smtpDto;
    }
}
