package com.example.smm_cms.dto.response.report;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class RevenueReportResponse {
    private Long totalOrders;

    private BigDecimal grossRevenue;

    private BigDecimal refundedAmount;

    private BigDecimal netRevenue;

    private BigDecimal totalCost;

    private BigDecimal profit;
}
