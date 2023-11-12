package com.example.clienteventservice.controller;

import com.example.clienteventservice.domain.dto.AmountDto;
import com.example.clienteventservice.domain.model.TransactionHistory;
import com.example.clienteventservice.domain.type.TransactionType;
import com.example.clienteventservice.service.DepositService;
import com.example.clienteventservice.service.TransactionService;
import com.example.clienteventservice.service.TransferService;
import com.example.clienteventservice.service.WithdrawService;
import com.example.clienteventservice.domain.response.ApiResponse;
import com.example.response.LoginResponse;
import com.google.common.base.Preconditions;
import io.swagger.annotations.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("Transfer services")
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private static final Logger LOG = LogManager.getLogger(TransactionController.class);
    private DepositService depositService;
    private TransferService transferService;
    private WithdrawService withdrawService;
    private TransactionService transactionService;

    @ApiOperation(value = "Withdraw from an account")
    @PostMapping(value = "/withdraw/{bankAccountId}")
    public ResponseEntity<?> withdraw(@ApiParam(value = "The ID of the bank account") @PathVariable(name = "bankAccountId") String bankAccountNumber,
                                   @ApiParam(value = "The amount of the withdraw transaction") @RequestBody @Valid AmountDto amountDto) {
        LOG.info("/{} called with amount: {}", bankAccountNumber, amountDto);
        Preconditions.checkNotNull(amountDto, "amountDto can not be null");

        withdrawService.withdraw(bankAccountNumber, amountDto.getAmount());

        return ResponseEntity.ok(new ApiResponse<>("Withdraw successfully",  HttpStatus.OK.value()));
    }

    @ApiOperation(value = "Transfer money from an account to other account")

    @PostMapping(value = "/transfer/{fromBankAccountId}/{toBankAccountId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<?> transfer(@ApiParam(value = "The ID of the from bank account") @PathVariable(name = "fromBankAccountId") String fromBankAccountNumber,
                         @ApiParam(value = "The ID of the to bank account") @PathVariable(name = "toBankAccountId") String toBankAccountNumber,
                         @ApiParam(value = "The amount of the withdraw transaction") @RequestBody @Valid AmountDto amountDto) {
        LOG.info("/{}/{} called with amount: {}", fromBankAccountNumber, toBankAccountNumber, amountDto);

        transferService.transfer(fromBankAccountNumber, toBankAccountNumber, amountDto.getAmount());
        return ResponseEntity.ok(new ApiResponse<>("Transfer successfully", HttpStatus.OK.value()));

    }

    @ApiOperation(value = "Deposit the money")

    @PostMapping(value = "/deposit/{accountNumber}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<?> deposit(
            @ApiParam(value = "The ID of the to bank account") @PathVariable(name = "accountNumber") String accountNumber,
            @ApiParam(value = "The amount of the withdraw transaction") @RequestBody @Valid AmountDto amountDto) {
        LOG.info("/{} called with amount: {}", accountNumber, amountDto);

        depositService.deposit(accountNumber, amountDto.getAmount());
        return ResponseEntity.ok(new ApiResponse<>("Deposit successfully", HttpStatus.OK.value()));

    }

    @ApiOperation(value = "Get transaction history")

    @GetMapping(value = "/history/{bankAccountNumber}")
    public ResponseEntity<?> getTransactionHistoryByAccountNumber(
            @ApiParam(value = "The ID of the to bank account") @PathVariable(name = "bankAccountNumber") String bankAccountNumber) {

       List<TransactionHistory> transactionHistories =  transactionService.getTransactionHistoryByAccountNumber(bankAccountNumber);
        return ResponseEntity.ok(new ApiResponse<>("Get data successfully",transactionHistories, HttpStatus.OK.value()));

    }



}
