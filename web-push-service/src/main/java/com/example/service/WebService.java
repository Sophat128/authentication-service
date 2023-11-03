package com.example.service;

import com.example.model.entities.WebDataConfig;
import com.example.model.request.WebConfigRequest;

import java.util.UUID;

public interface WebService {
    void addConfig(WebConfigRequest webConfigRequest);

    WebDataConfig updateConfig(UUID id, WebConfigRequest webConfigRequest);

    WebDataConfig getConfigById(UUID id);
    WebDataConfig getConfig();

}
