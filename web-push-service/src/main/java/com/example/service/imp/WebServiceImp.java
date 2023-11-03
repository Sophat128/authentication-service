package com.example.service.imp;

import com.example.exception.NotFoundException;
import com.example.model.entities.WebDataConfig;
import com.example.model.request.WebConfigRequest;
import com.example.repository.WebConfigRepository;
import com.example.service.WebService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class WebServiceImp implements WebService {
    private final WebConfigRepository webConfigRepository;

    @Override
    public void addConfig(WebConfigRequest webConfigRequest) {
        webConfigRepository.save(webConfigRequest.toEntity());

    }

    @Override
    public WebDataConfig updateConfig(UUID id, WebConfigRequest webConfigRequest) {
        WebDataConfig updateConfig = getConfigById(id);
        updateConfig.setPrivateKey(webConfigRequest.getPrivateKey());
        updateConfig.setPublicKey(webConfigRequest.getPublicKey());
        return webConfigRepository.save(updateConfig);
    }
    @Override
    public WebDataConfig getConfigById(UUID id) {
        return webConfigRepository.findById(id).orElseThrow(() -> new NotFoundException("WebDataConfig with ID " + id + " not found"));
    }

    @Override
    public WebDataConfig getConfig() {
        return webConfigRepository.findAll().get(0);

    }
}
