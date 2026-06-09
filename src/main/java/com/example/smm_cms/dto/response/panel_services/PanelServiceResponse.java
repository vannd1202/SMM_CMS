package com.example.smm_cms.dto.response.panel_services;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PanelServiceResponse {
    private Long id;

    private String name;

    private String description;

    private BigDecimal sellPrice;

    private Boolean active;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
