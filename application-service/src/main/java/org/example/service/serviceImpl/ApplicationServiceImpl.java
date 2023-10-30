package org.example.service.serviceImpl;

import com.example.dto.UserDto;

import org.example.exception.ForbiddenException;
import org.example.exception.NotFoundException;
import org.example.model.Application;
import org.example.model.PlatformType;
import com.example.dto.ApplicationDto;
import org.example.model.request.ApplicationRequest;
import org.example.repository.ApplicationRepository;
import org.example.service.ApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final WebClient webClient;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository, WebClient webClient) {
        this.applicationRepository = applicationRepository;
        this.webClient = webClient;
    }


    public UserDto getUserById(UUID userId) {
        return webClient.get()
                .uri("/users/{userId}", userId)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    @Override
    public ApplicationDto createNewApp(ApplicationRequest applicationRequest, PlatformType platformType, Principal principal) {
        if(principal == null){
            throw new ForbiddenException("Need Token");
        }
        UUID userId = UUID.fromString(principal.getName());
        Application application = new Application();
        application.setName(applicationRequest.getName());
        application.setPlatformType(String.valueOf(platformType));
        application.setUserId(userId);
        application.setCreatedDate(LocalDateTime.now());
        applicationRepository.save(application);
        ApplicationDto dto = new ApplicationDto();
        UserDto userById =getUserById(userId);
        dto.setUserDto(userById);
        dto.setId(application.getId());
        dto.setName(application.getName());
        dto.setPlatformType(application.getPlatformType());
        dto.setCreatedDate(application.getCreatedDate());
        return dto;
    }

    @Override
    public List<ApplicationDto> getAllApp(Principal principal) {
        if (principal == null) {
            throw new ForbiddenException("need token");
        }
        UUID userId = UUID.fromString(principal.getName());
        List<Application> applicationList = applicationRepository.findByUserId(userId);
        List<ApplicationDto> applicationDtoList =new ArrayList<>();
        for (Application application: applicationList) {
            UserDto userById =getUserById(userId);
            ApplicationDto dto = new ApplicationDto();
            dto.setUserDto(userById);
            dto.setId(application.getId());
            dto.setName(application.getName());
            dto.setPlatformType(application.getPlatformType());
            dto.setCreatedDate(application.getCreatedDate());
            applicationDtoList.add(dto);
        }
        return applicationDtoList;
    }

    @Override
    public ApplicationDto getApplicationById(UUID id, Principal principal) {
        if (principal == null) {
            throw new ForbiddenException("Need token");
        }

        UUID userId = UUID.fromString(principal.getName());

        Application application = applicationRepository.findApplicationByIdAndUserId(id, userId);
        if (application != null){
            UserDto userById =getUserById(userId);
            ApplicationDto dto = new ApplicationDto();
            dto.setUserDto(userById);
            dto.setId(application.getId());
            dto.setName(application.getName());
            dto.setPlatformType(application.getPlatformType());
            dto.setCreatedDate(application.getCreatedDate());
            return dto;
        }else{
            throw new NotFoundException("application not found");
        }
    }

    @Override
    public String deleteApp(UUID id, Principal principal) {
        if (principal == null) {
            throw new ForbiddenException("Need token");
        }
        UUID userId = UUID.fromString(principal.getName());
        Application application = applicationRepository.findApplicationByIdAndUserId(id, userId);
        if (application!=null){
            applicationRepository.deleteById(id);
            return "delete successfully " ;
        }else{
            throw new NotFoundException("application not found");
        }
    }

    @Override
    public ApplicationDto updateApp(ApplicationRequest applicationRequest, UUID id, Principal principal) {
        if (principal == null) {
            throw new ForbiddenException("Need token");
        }
        UUID userId = UUID.fromString(principal.getName());
        Application application = applicationRepository.findApplicationByIdAndUserId(id, userId);
        if (application!=null){
            application.setName(applicationRequest.getName());
            Application updateApp = applicationRepository.save(application);
            ApplicationDto dto = new ApplicationDto();
            UserDto userById =getUserById(userId);
            dto.setUserDto(userById);
            dto.setId(application.getId());
            dto.setName(application.getName());
            dto.setPlatformType(application.getPlatformType());
            dto.setCreatedDate(application.getCreatedDate());
            return dto;
        }else{
            throw new NotFoundException("application not found");
        }
    }

    @Override
    public List<ApplicationDto> getApplicationByType(String platformType, Principal principal) {
        if (principal == null) {
            throw new ForbiddenException("need token");
        }
        UUID userId = UUID.fromString(principal.getName());
        List<Application> applicationList = applicationRepository.findByPlatformAndUserId(platformType,userId);
        List<ApplicationDto> applicationDtoList =new ArrayList<>();
        for (Application application: applicationList) {
            UserDto userById =getUserById(userId);
            ApplicationDto dto = new ApplicationDto();
            dto.setUserDto(userById);
            dto.setId(application.getId());
            dto.setName(application.getName());
            dto.setPlatformType(application.getPlatformType());
            dto.setCreatedDate(application.getCreatedDate());
            applicationDtoList.add(dto);

        }
        return applicationDtoList;
    }
}

