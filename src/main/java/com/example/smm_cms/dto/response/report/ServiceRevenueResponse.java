package com.example.smm_cms.dto.response.report;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ServiceRevenueResponse {
    /**
     * ID dịch vụ nội bộ
     */
    private Long panelServiceId;

    /**
     * Tên dịch vụ
     */
    private String panelServiceName;

    /**
     * Tổng số đơn
     */
    private Long totalOrders;

    /**
     * Tổng số lượng đã bán
     */
    private Long totalQuantity;

    /**
     * Doanh thu
     */
    private BigDecimal revenue;

    /**
     * Giá vốn
     */
    private BigDecimal cost;

    /**
     * Lợi nhuận
     */
    private BigDecimal profit;

    /**
     * Giá bán trung bình / 1000
     */
    private BigDecimal avgSellPrice;

    /**
     * Giá vốn trung bình / 1000
     */
    private BigDecimal avgCostPrice;
}
