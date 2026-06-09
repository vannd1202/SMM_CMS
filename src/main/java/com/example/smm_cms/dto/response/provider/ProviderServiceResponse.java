package com.example.smm_cms.dto.response.provider;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProviderServiceResponse {
    private Long service;
    private String name;
    private String type;
    private String category;
    private BigDecimal rate;
    private Integer min;
    private Integer max;
    private Boolean refill;
    private Boolean cancel;
}
