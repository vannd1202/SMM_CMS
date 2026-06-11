package com.example.smm_cms.dto.response.provider;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProviderBalanceResponse {

    private BigDecimal balance;

    private String currency;

    private String error;
}
