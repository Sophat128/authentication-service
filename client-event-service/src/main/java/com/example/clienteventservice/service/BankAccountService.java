package com.example.clienteventservice.service;

import com.example.clienteventservice.event.SBAEventListener;
import com.example.clienteventservice.exception.BankAccountManagerException;
import com.example.clienteventservice.repository.BankAccountRepository;
import com.example.clienteventservice.domain.dto.BankAccountDto;
import com.example.clienteventservice.domain.model.BankAccount;
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

    public BankAccount addBankAccount(UUID customerId, BankAccountDto bankAccountDto) {
        BankAccount bankAccount = bankAccountDto.toEntity();

        Preconditions.checkNotNull(bankAccountDto, "bankAccount can not be null");
        Preconditions.checkArgument(
                bankAccountDto.getAccountNumber().matches("\\d{10}"),
                "Bank AccountNumber must be 9 digits"
        );

        Preconditions.checkNotNull(bankAccountDto.getBalance(), "currentBalance can not be null");
        Preconditions.checkArgument(
                bankAccountDto.getBalance().compareTo(BigDecimal.ZERO) > -1 && bankAccountDto.getBalance().compareTo(new BigDecimal("5")) >= 0,
                "CurrentBalance must be non-negative and at least $5"
        );

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


//    private String formatAccountNumber(String accountNumber) {
//        if (accountNumber == null) {
//            return null;
//        }
//
//        // Check if the accountNumber has 9 digits
//        if (accountNumber.matches("\\d{9}")) {
//            // Format with spaces for every three digits
//            Pattern pattern = Pattern.compile("(\\d{3})(\\d{3})(\\d{3})");
//            Matcher matcher = pattern.matcher(accountNumber);
//
//            if (matcher.matches()) {
//                return matcher.group(1) + " " + matcher.group(2) + " " + matcher.group(3);
//            }
//        }
//
//        return accountNumber;
//    }

//    private static BigDecimal formatBalance(BigDecimal balance) {
//        if (balance == null) {
//            return null;
//        }
//
//        DecimalFormat decimalFormat;
//
//        // Check if the balance is very large (1 billion dollars or more)
//        if (balance.abs().compareTo(new BigDecimal("1000000000")) >= 0) {
//            decimalFormat = new DecimalFormat("$#,##0");
//        } else {
//            decimalFormat = new DecimalFormat("$#,##0.00");
//        }
//
//        String formattedString = decimalFormat.format(balance);
//        return new BigDecimal(formattedString.replaceAll("[^\\d.]+", ""));
//    }

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
