package com.example.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * bank_account table java object
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    private Long id;

    private String accountNumber;

    @NonNull
    private BigDecimal currentBalance;

    private UUID customerId;


    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;


}
