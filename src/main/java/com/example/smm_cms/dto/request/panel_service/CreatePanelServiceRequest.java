package com.example.smm_cms.dto.request.panel_service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePanelServiceRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BigDecimal sellPrice;

    private Boolean active;

}
