package com.example.controller;

import com.example.entities.request.TransferRequest;
import com.example.entities.response.TransferResponse;
import com.example.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transaction")
    public TransferResponse transaction(@RequestBody TransferRequest transferRequest){
        return transactionService.transfer(transferRequest);
    }
}
