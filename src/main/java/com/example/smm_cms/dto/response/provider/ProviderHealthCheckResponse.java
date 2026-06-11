package com.example.smm_cms.dto.response.provider;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ProviderHealthCheckResponse {
    private Long providerId;

    private String providerName;

    private String status;

    private BigDecimal balance;

    private Long responseTimeMs;
}
