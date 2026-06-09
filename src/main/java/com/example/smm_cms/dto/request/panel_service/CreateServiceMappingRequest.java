package com.example.smm_cms.dto.request.panel_service;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateServiceMappingRequest {
    @NotNull
    private Long panelServiceId;

    @NotNull
    private Long providerServiceId;

    @NotNull
    private Integer priority;
}
