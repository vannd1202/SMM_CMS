package com.example.smm_cms.dto.response.provider;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProviderRequest {
    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    private String name;

    @NotBlank(message = "URL không được để trống")
    private String apiUrl;

    @NotBlank(message = "Api key không được để trống")
    private String apiKey;
}
