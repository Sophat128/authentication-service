package org.example.service;

import org.example.model.PlatformType;
import com.example.dto.ApplicationDto;
import org.example.model.request.ApplicationRequest;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    ApplicationDto createNewApp(ApplicationRequest applicationRequest, PlatformType platformType, Principal principal);

    List<ApplicationDto> getAllApp(Principal principal);

    ApplicationDto getApplicationById(UUID id, Principal principal);

    String deleteApp(UUID id, Principal principal);

    ApplicationDto updateApp(ApplicationRequest applicationRequest, UUID id, Principal principal);

    List<ApplicationDto> getApplicationByType(String platformType, Principal principal);
}
