package com.example.smm_cms.dto.request.wallet;

import com.example.smm_cms.common.TransactionType;
import com.example.smm_cms.dto.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SearchWalletTransactionRequest extends PageRequest {
    private Long userId;

    private TransactionType type;

    private LocalDate fromDate;

    private LocalDate toDate;
}
