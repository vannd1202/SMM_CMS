package com.example.smm_cms.dto.request.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull
    private Long panelServiceId;

    @NotBlank
    private String target;

    @Min(1)
    private Integer quantity;

    private Long userId;


}
