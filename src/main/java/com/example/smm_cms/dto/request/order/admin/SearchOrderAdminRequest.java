package com.example.smm_cms.dto.request.order.admin;

import com.example.smm_cms.common.OrderStatus;
import com.example.smm_cms.dto.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SearchOrderAdminRequest extends PageRequest {
    private Long id;

    private String providerOrderId;

    private Long userId;

    private Long panelServiceId;

    private OrderStatus status;

    private LocalDate fromDate;

    private LocalDate toDate;
}
