package com.example.smm_cms.dto.response.panel_services;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePanelServiceResponse {
    private Long id;

    private String name;

    private BigDecimal sellPrice;

    private Boolean active;
}
