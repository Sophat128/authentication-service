package com.example.clienteventservice.controller;

import com.example.clienteventservice.domain.dto.BalanceDto;
import com.example.clienteventservice.domain.dto.BankAccountDto;
import com.example.clienteventservice.domain.model.BankAccount;
import com.example.clienteventservice.event.SBAEventListener;
import com.example.clienteventservice.service.BankAccountService;
import com.google.common.base.Preconditions;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class is created to manage bank account process
 */
@Api("Bank account management services")
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping(BankAccountController.SERVICE_PATH)
public class BankAccountController {

    public static final String SERVICE_PATH = "api/v1/bank/";
    public static final String METHOD_GET_BALANCE = "/balance";
    private static final String METHOD_GET_BALANCE_WITH_PARAM = "/balance/{bankAccountNumber}";
    public static final String METHOD_GET_BALANCE_ALL = "/balance/all";

    private BankAccountService bankAccountService;
    private ConversionService conversionService;
    private static final Logger LOG = LogManager.getLogger(BankAccountController.class);


    @ApiOperation(value = "Create a new bank account with a credit card or debit card by given customerId")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request."),
            @ApiResponse(code = 500, message = "Internal Error.")
    })
    @PutMapping(value = "{customerId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void saveAccount(@ApiParam(value = "The ID of the customer") @PathVariable(name = "customerId") UUID customerId,
                            @ApiParam(value = "The number of the customer") @RequestBody BankAccountDto bankAccountDto) {
        LOG.info("/{}/{} called with bankAccountDto: {}", SERVICE_PATH, customerId, bankAccountDto);
        // we used checkArgument instead of checkNotNull because we want an IllegalArgumentException
        Preconditions.checkArgument(bankAccountDto != null, "bankAccountDto can not be null");
//        Preconditions.checkArgument(bankAccountDto.getCard() != null, "bankAccountDto.card can not be null");

//        BankAccount bankAccount = conversionService.convert(bankAccountDto, BankAccount.class);

        bankAccountService.addBankAccount(customerId, bankAccountDto);
    }
    @ApiOperation(value = "Retrieves the current balances of all bank accounts", response = BalanceDto.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK."),
            @ApiResponse(code = 400, message = "Bad Request."),
            @ApiResponse(code = 500, message = "Internal Error.")
    })
    @GetMapping(value = METHOD_GET_BALANCE_ALL)
    public List<BalanceDto> getAllBalances() {
        LOG.info("/{}{} called", SERVICE_PATH, METHOD_GET_BALANCE_ALL);

        return bankAccountService.getBankAccountList().stream()
                .map(bankAccount -> conversionService.convert(bankAccount, BalanceDto.class))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "Retrieves the current balance of a bank account", response = BalanceDto.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK."),
            @ApiResponse(code = 400, message = "Bad Request."),
            @ApiResponse(code = 500, message = "Internal Error.")
    })
    @GetMapping(value = METHOD_GET_BALANCE_WITH_PARAM)
    public BalanceDto getBalance(@ApiParam(value = "The ID of the bank account") @PathVariable(name = "bankAccountNumber") String bankAccountNumber) {
        LOG.info("/{}{}/{} called", SERVICE_PATH, METHOD_GET_BALANCE, bankAccountNumber);
        BankAccount bankAccount = bankAccountService.getBankAccount(bankAccountNumber);
        return conversionService.convert(bankAccount, BalanceDto.class);
    }

    @ApiOperation(value = "Retrieves bank info with user id", response = BalanceDto.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK."),
            @ApiResponse(code = 400, message = "Bad Request."),
            @ApiResponse(code = 500, message = "Internal Error.")
    })
    @GetMapping("/bankInfo/{userId}")
    public BalanceDto getAccountInfoByUserId(@ApiParam(value = "The ID of the bank account") @PathVariable(name = "userId") UUID userId) {
        BankAccount bankAccount = bankAccountService.getBankAccountByUserId(userId);
        return conversionService.convert(bankAccount, BalanceDto.class);
    }

    @ApiOperation(value = "Retrieves customer info with bank account number", response = BankAccount.class)

    @GetMapping("/customerInfo/{bankAccountNo}")
    public ResponseEntity<?> getAccountInfoByUserId(@ApiParam(value = "The ID of the bank account") @PathVariable(name = "bankAccountNo") String bankAccountNo) {
        BankAccount bankAccount = bankAccountService.getBankAccount(bankAccountNo);
        return ResponseEntity.ok(bankAccount);
    }



}
