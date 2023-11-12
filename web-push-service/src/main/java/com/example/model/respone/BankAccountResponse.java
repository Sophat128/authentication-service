package com.example.model.respone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountResponse {
    private Long id;

    private String accountNumber;
    private BigDecimal currentBalance;
    private String customerId;
    private Date createdAt;

    private Date updatedAt;
}
