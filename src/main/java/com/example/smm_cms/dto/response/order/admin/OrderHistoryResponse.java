package com.example.smm_cms.dto.response.order.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OrderHistoryResponse {
    private Long id;

    private String oldStatus;

    private String newStatus;

    private String note;

    private LocalDateTime createdDate;
}
