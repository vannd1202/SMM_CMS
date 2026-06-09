package com.example.smm_cms.dto.response.service;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMappingResponse {
    private Long mappingId;

    private Integer priority;

    private Long providerId;

    private String providerName;

    private Long providerServiceId;

    private String providerServiceName;

    private BigDecimal rate;
}
