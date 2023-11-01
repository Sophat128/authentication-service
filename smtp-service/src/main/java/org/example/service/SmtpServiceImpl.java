package org.example.service;

import com.example.dto.ApplicationDto;
import com.example.dto.SmtpDto;
import com.example.response.ApiResponse;
import org.example.entity.Smtp;
import org.example.entity.request.SmtpRequest;
import org.example.exception.BadRequestException;
import org.example.exception.ForbiddenException;
import org.example.exception.NotFoundException;
import org.example.repository.SmtpRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
    public ApplicationDto getAppById(UUID appId, Jwt jwt) {
        try {
            return webClient.get()
                    .uri("/application/{appId}", appId)
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(jwt.getTokenValue()))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<ApplicationDto>>() {})
                    .block()
                    .getPayload();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("Application not found."); // or handle it in a way you prefer
            } else {
                throw e;
            }
        }
    }

    @Override
    public SmtpDto setUpSmtp(SmtpRequest smtpRequest, Principal principal, Jwt jwt, UUID appId) {
        if (principal.getName() == null) {
            throw new ForbiddenException("Need token");
        }

        UUID userId = UUID.fromString(principal.getName());
        ApplicationDto appById = getAppById(appId, jwt);

        if (!userId.equals(UUID.fromString(String.valueOf(appById.getUserDto().getId())))){
            throw new ForbiddenException("User is not the owner of the application.");
        }

        Smtp existingSmtp = smtpRepository.findByAppId(appId);

        if (existingSmtp != null) {
            throw new ForbiddenException("SMTP configuration already exists for this application.");
        }

            if (smtpRequest.getUsername().isBlank() || smtpRequest.getUsername().isEmpty()) {
                throw new BadRequestException("Field username can't be blank");
            }

            if (!smtpRequest.getUsername().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,6}")) {
                throw new BadRequestException("Username should be like this -> something@something.com");
            }

            if (smtpRequest.getPassword().isBlank() || smtpRequest.getPassword().isEmpty()) {
                throw new BadRequestException("Field password can't be blank");
            }

            Smtp smtp = new Smtp();
            smtp.setUsername(smtpRequest.getUsername());
            smtp.setPassword(smtpRequest.getPassword());
            smtp.setAppId(appId);
            smtpRepository.save(smtp);

            SmtpDto smtpDto = new SmtpDto();
            smtpDto.setId(smtp.getId());
            smtpDto.setUsername(smtp.getUsername());
            smtpDto.setPassword(smtp.getPassword());
            smtpDto.setApplicationDto(appById);

            return smtpDto;

    }


    @Override
    public SmtpDto getSmtpById(UUID id, UUID appId, Principal principal, Jwt jwt) {
        if (principal.getName()==null){
            throw new ForbiddenException("need token");
        }
        Smtp smtp = smtpRepository.findBySmtpById(id,appId);
        if (smtp != null){
            SmtpDto smtpDto = new SmtpDto();
            smtpDto.setId(smtp.getId());
            smtpDto.setUsername(smtp.getUsername());
            smtpDto.setPassword(smtp.getPassword());
            ApplicationDto appById =getAppById(smtp.getAppId(),jwt);
            smtpDto.setApplicationDto(appById);
            return smtpDto;
        }else {
            throw new NotFoundException("smtp id: " +id+ " of application id : "+appId+" not found");
        }
    }

    @Override
    public SmtpDto updateSmtpById(UUID id, UUID appId, SmtpRequest smtpRequest, Principal principal, Jwt jwt) {
        if(principal.getName() == null){
            throw new ForbiddenException("need token");
        }
        Smtp smtp = smtpRepository.findBySmtpById(id,appId);
        if (smtp!=null){
            if (smtpRequest.getUsername().isBlank()||smtpRequest.getUsername().isEmpty()){
                throw new BadRequestException("Field username can't be blank");
            }
            if (!smtpRequest.getUsername().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,6}")) {
                throw new BadRequestException(
                        "Username should be like this -> somthing@somthing.com"
                );
            }
            if (smtpRequest.getPassword().isBlank()||smtpRequest.getPassword().isEmpty()){
                throw new BadRequestException("Field password can't be blank");
            }
            smtp.setUsername(smtpRequest.getUsername());
            smtp.setPassword(smtp.getPassword());
            smtpRepository.save(smtp);
            SmtpDto smtpDto = new SmtpDto();
            smtpDto.setId(smtp.getId());
            smtpDto.setUsername(smtp.getUsername());
            smtpDto.setPassword(smtp.getPassword());
            ApplicationDto appById = getAppById(appId, jwt);
            smtpDto.setApplicationDto(appById);
            return null;
        }else{
            throw new NotFoundException("smtp id: " +id+ " of application id : "+appId+" not found");
        }
    }
}
