package com.example.smm_cms.dto.request.smm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmmRequest {
    private String key;
    private String action;

    private String service;
    private String link;
    private Integer quantity;
}
