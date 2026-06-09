package com.example.smm_cms.dto.response.order;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderResponse {
    private Long id;

    private String providerOrderId;

    private String status;

    private BigDecimal amount;

}
