package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.dto.BankAccountDto;
import com.example.clienteventservice.domain.model.BankAccount;
import com.example.clienteventservice.event.SBAEventListener;
import com.example.clienteventservice.exception.BankAccountManagerException;
import com.example.clienteventservice.repository.BankAccountRepository;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * BankAccount management service
 */
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class BankAccountService {
    private static final Logger LOG = LogManager.getLogger(SBAEventListener.class);


    private static final String MESSAGE_FORMAT_NO_BANK_ACCOUNT = "No bankAccount by bankAccountId: %s";

    private BankAccountRepository bankAccountRepository;
    private CustomerService customerService;

    public BankAccount addBankAccount(UUID customerId, BankAccountDto bankAccountDto) {
        BankAccount bankAccount = bankAccountDto.toEntity();

        Preconditions.checkNotNull(bankAccountDto, "bankAccount can not be null");
        Preconditions.checkNotNull(bankAccountDto.getBalance(), "currentBalance can not be null");
        Preconditions.checkArgument(bankAccountDto.getBalance().compareTo(BigDecimal.ZERO) > -1, "CurrentBalance can not be negative");

//        Customer customer = customerService.getCustomer(customerId);
        bankAccount.setCustomerId(customerId);

//        Card card = bankAccount.getCard();
//        card.setHolderName(FormatterUtil.getCardHolderName(customer));
//
//        // a workaround
//        bankAccount.setCard(null);
        BankAccount savedBankAccount = bankAccountRepository.save(bankAccount);
        LOG.info("A bank account saved for customer: {}", customerId);

//        card.setBankAccount(savedBankAccount);
//        cardRepository.save(card);

        return savedBankAccount;
    }

    public BankAccount getBankAccount(String bankAccountNumber) {
        Preconditions.checkNotNull(bankAccountNumber, MESSAGE_FORMAT_NO_BANK_ACCOUNT, bankAccountNumber);

        return bankAccountRepository.findByAccountNumber(bankAccountNumber)
                .orElseThrow(() -> BankAccountManagerException.to(MESSAGE_FORMAT_NO_BANK_ACCOUNT, bankAccountNumber));
    }


    public List<BankAccount> getBankAccountList() {
        return bankAccountRepository.findAll();
    }

    public BankAccount decreaseCurrentBalance(BankAccount bankAccount, BigDecimal amount) {
        int effectedRows = bankAccountRepository.decreaseCurrentBalance(bankAccount.getId(), amount);
        if (effectedRows == 0) {
            throw BankAccountManagerException.to(
                    "The bank account is not effected of withdraw");
        }

        return bankAccountRepository.findById(bankAccount.getId())
                .orElseThrow(() -> BankAccountManagerException.to(MESSAGE_FORMAT_NO_BANK_ACCOUNT, bankAccount.getId()));
    }

    public BankAccount increaseCurrentBalance(BankAccount bankAccount, BigDecimal amount) {
        int effectedRows = bankAccountRepository.increaseCurrentBalance(bankAccount.getId(), amount);
        if (effectedRows == 0) {
            throw BankAccountManagerException.to(
                    "The bank account is not effected of transfer");
        }

        return bankAccountRepository.findById(bankAccount.getId())
                .orElseThrow(() -> BankAccountManagerException.to(MESSAGE_FORMAT_NO_BANK_ACCOUNT, bankAccount.getId()));
    }

    public BankAccount getBankAccountByUserId(UUID userId) {
        Preconditions.checkNotNull(userId, MESSAGE_FORMAT_NO_BANK_ACCOUNT, userId);

        return bankAccountRepository.findByCustomerId(userId)
                .orElseThrow(() -> BankAccountManagerException.to(MESSAGE_FORMAT_NO_BANK_ACCOUNT, userId));
    }
}
