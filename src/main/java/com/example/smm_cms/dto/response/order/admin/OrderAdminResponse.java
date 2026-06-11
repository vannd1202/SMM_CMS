package com.example.smm_cms.dto.response.order.admin;

import com.example.smm_cms.common.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class OrderAdminResponse {
    private Long id;

    private Long userId;

    private String username;

    private String panelServiceName;

    private String providerName;

    private String providerOrderId;

    private String target;

    private Integer quantity;

    private BigDecimal amount;

    private OrderStatus status;

    private Boolean refunded;

    private LocalDateTime createdDate;
}
