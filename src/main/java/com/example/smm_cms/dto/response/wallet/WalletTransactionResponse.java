package com.example.smm_cms.dto.response.wallet;

import com.example.smm_cms.common.TransactionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class WalletTransactionResponse {
    private Long id;

    private Long userId;

    private String username;

    private TransactionType type;

    private BigDecimal amount;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;

    private Long referenceId;

    private String note;

    private LocalDateTime createdDate;
}
