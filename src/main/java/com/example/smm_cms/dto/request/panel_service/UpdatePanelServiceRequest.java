package com.example.smm_cms.dto.request.panel_service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdatePanelServiceRequest {
    @NotBlank(message = "Tên dịch vụ không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Giá bán phải lớn hơn 0")
    private BigDecimal sellPrice;

    private Boolean active;
}
