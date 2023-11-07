package com.example.controller;

import com.example.entities.request.TransferRequest;
import com.example.entities.response.TransferResponse;
import com.example.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public TransferResponse transaction(@RequestBody TransferRequest transferRequest){
        return transactionService.transfer(transferRequest);
    }
}
