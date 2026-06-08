package com.example.smm_cms.dto.request.provider;

import com.example.smm_cms.common.ProviderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProviderResponse {
    private Long id;

    private String name;

    private String apiUrl;

    private ProviderStatus status;

    private LocalDateTime createdAt;
}
