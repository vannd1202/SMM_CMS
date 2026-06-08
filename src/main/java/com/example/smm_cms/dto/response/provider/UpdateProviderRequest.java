package com.example.smm_cms.dto.response.provider;

import com.example.smm_cms.common.ProviderStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProviderRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String apiUrl;

    @NotBlank
    private String apiKey;

    private ProviderStatus status;
}
