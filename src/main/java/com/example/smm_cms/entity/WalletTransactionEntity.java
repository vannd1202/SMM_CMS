package com.example.smm_cms.entity;

import com.example.smm_cms.common.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transaction")
@Getter
@Setter
public class WalletTransactionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private BigDecimal amount;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String note;

    private Long referenceId;

    private LocalDateTime createdDate;
}
