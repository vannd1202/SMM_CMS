package com.example.smm_cms.dto.request.wallet;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopupRequest {
    private Long userId;
    private BigDecimal amount;
}
