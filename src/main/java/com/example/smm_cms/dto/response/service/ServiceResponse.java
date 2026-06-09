package com.example.smm_cms.dto.response.service;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {
    private Long id;

    private Long providerServiceId;

    private String name;

    private String category;

    private String type;

    private BigDecimal rate;

    private Integer minQuantity;

    private Integer maxQuantity;

    private Boolean refill;

    private Boolean cancelable;

    private Boolean active;
}
