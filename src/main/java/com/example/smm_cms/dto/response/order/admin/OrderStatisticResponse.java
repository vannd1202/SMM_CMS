package com.example.smm_cms.dto.response.order.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class OrderStatisticResponse {
    /**
     * Đơn vừa tạo, chưa gửi provider
     */
    private Long creating;

    /**
     * Đã gửi provider
     */
    private Long pending;

    /**
     * Đang chạy
     */
    private Long processing;

    /**
     * Hoàn thành
     */
    private Long completed;

    /**
     * Hoàn thành một phần
     */
    private Long partial;

    /**
     * Đã hủy
     */
    private Long cancelled;

    /**
     * Thất bại
     */
    private Long failed;

    /**
     * Đã hoàn tiền
     */
    private Long refunded;

    /**
     * Tổng số đơn
     */
    private Long total;

    /*
     * Revenue = số tiền khách trả
     */
    private BigDecimal totalRevenue;

    /*
     * Cost = số tiền trả provider
     */
    private BigDecimal totalCost;

    /*
     * Profit = Revenue - Cost
     */
    private BigDecimal totalProfit;
}
