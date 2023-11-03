package com.example.service;

import com.example.entities.request.TransferRequest;
import com.example.entities.response.TransferResponse;

public interface TransactionService {
    TransferResponse transfer(TransferRequest transferRequest);
}
