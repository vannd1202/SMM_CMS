package com.example.smm_cms.dto.response.provider;

import lombok.Data;

@Data
public class ProviderServiceResponse {
    private Long service;
    private String name;
    private String type;
    private String category;
    private String rate;
    private String min;
    private String max;
    private Boolean refill;
    private Boolean cancel;
}
