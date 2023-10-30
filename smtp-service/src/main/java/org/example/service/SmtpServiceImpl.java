package org.example.service;

import com.example.dto.ApplicationDto;
import com.example.dto.SmtpDto;
import com.example.response.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import org.example.entity.Smtp;
import org.example.entity.request.SmtpRequest;
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
                throw e; // rethrow other WebClientResponseException
            }
        }
    }
    private void checkUserIsOwnerOfApp(UUID userId, ApplicationDto appDto) {
        if (!userId.equals(UUID.fromString(String.valueOf(appDto.getUserDto().getId())))) {
            throw new ForbiddenException("User is not the owner of the application.");
        }
    }



    @Override
    public SmtpDto setUpSmtp(@NotBlank SmtpRequest smtpRequest, Principal principal, Jwt jwt, UUID appId) {
        if (principal.getName()==null){
            throw new ForbiddenException("need token");
        }
        UUID userId = UUID.fromString(principal.getName());
        ApplicationDto appById = getAppById(appId, jwt);
        if (!userId.equals(UUID.fromString(String.valueOf(appById.getUserDto().getId())))){
            throw new ForbiddenException("User is not the owner of the application.");
        }
        if ("email".equalsIgnoreCase(appById.getPlatformType())) {
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
        } else {
            throw new ForbiddenException("Invalid platform type for creating SMTP");
        }
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
