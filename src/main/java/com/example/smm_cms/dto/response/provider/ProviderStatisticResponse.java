package com.example.smm_cms.dto.response.provider;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProviderStatisticResponse {
    private Long providerId;

    private String providerName;

    private String apiUrl;

    private Integer totalServices;

    private Long totalOrders;

    private Long failedOrders;

    private BigDecimal balance;

    private LocalDateTime lastSyncTime;

    private Enum active;
}
