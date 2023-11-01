package com.example.controller;

import com.example.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/transaction")
    public String transaction(){
        return transactionService.transfer("20000");
    }
}
