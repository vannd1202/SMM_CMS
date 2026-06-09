package com.example.smm_cms.dto.response.order;

import com.example.smm_cms.common.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;

    private Long panelServiceId;

    private String panelServiceName;

    private String providerOrderId;

    private String target;

    private Integer quantity;

    private BigDecimal costPrice;

    private BigDecimal sellPrice;

    private BigDecimal amount;

    private Integer startCount;

    private Integer remains;

    private String providerStatus;

    private OrderStatus status;

    private LocalDateTime createdDate;
}
