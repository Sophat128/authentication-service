package com.example.service;

import com.example.model.entities.WebDataConfig;
import com.example.model.request.WebConfigRequest;
import com.example.model.respone.BankAccountResponse;

import java.security.GeneralSecurityException;
import java.util.UUID;

public interface WebService {
    void addConfig(WebConfigRequest webConfigRequest) throws GeneralSecurityException;

    WebDataConfig updateConfig(UUID id, WebConfigRequest webConfigRequest);

    WebDataConfig getConfigById(UUID id);
    WebDataConfig getConfig();
    BankAccountResponse getCustomerInfoByBankAccountNo(String accountNo);

}
