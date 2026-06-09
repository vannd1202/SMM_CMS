package com.example.smm_cms.dto.request.order;

import com.example.smm_cms.common.OrderStatus;
import com.example.smm_cms.dto.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchOrderRequest extends PageRequest {
    private Long panelServiceId;

    private String providerOrderId;

    private OrderStatus status;

    private String target;
}
