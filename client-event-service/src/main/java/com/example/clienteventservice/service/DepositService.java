package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.model.BankAccount;
import com.example.clienteventservice.domain.model.TransactionHistory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class DepositService {
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;
    public void deposit(String toBankAccountNumber, BigDecimal amount) {
        BankAccount toBankAccount = bankAccountService.getBankAccount(toBankAccountNumber);

        transactionService.executeDeposit(toBankAccount, amount);
    }


}
