package com.example.smm_cms.service.impl;

import com.example.smm_cms.base.BaseService;
import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.dto.request.report.RevenueReportRequest;
import com.example.smm_cms.dto.response.report.RevenueReportResponse;
import com.example.smm_cms.dto.response.report.ServiceRevenueResponse;
import com.example.smm_cms.entity.OrderEntity;
import com.example.smm_cms.repository.OrderRepository;
import com.example.smm_cms.repository.WalletTransactionRepository;
import com.example.smm_cms.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl extends BaseService implements IReportService {
        private final OrderRepository orderRepository;
        private final WalletTransactionRepository walletTransactionRepository;

    @Override
    public ResponseData<?> revenueReport(RevenueReportRequest request) {
        String logPrefix =
                "[ReportServiceImpl][revenueReport] - ";

        ResponseData<RevenueReportResponse>
                responseData = new ResponseData<>();

        try {

            LocalDateTime from =
                    request.getFromDate()
                            .atStartOfDay();

            LocalDateTime to =
                    request.getToDate()
                            .atTime(
                                    23,
                                    59,
                                    59);

            Long totalOrders =
                    orderRepository.countOrdersReport(
                            from,
                            to);

            BigDecimal grossRevenue =
                    Optional.ofNullable(
                                    orderRepository.sumRevenueReport(
                                            from,
                                            to))
                            .orElse(
                                    BigDecimal.ZERO);

            BigDecimal refundedAmount =
                    Optional.ofNullable(
                                    walletTransactionRepository
                                            .sumRefund(
                                                    from,
                                                    to))
                            .orElse(
                                    BigDecimal.ZERO);

            List<OrderEntity> orders =
                    orderRepository.findByCreatedDateBetween(
                            from,
                            to);

            BigDecimal totalCost =
                    orders.stream()
                            .map(order ->
                                    order.getCostPrice()
                                            .multiply(
                                                    BigDecimal.valueOf(
                                                            order.getQuantity()))
                                            .divide(
                                                    BigDecimal.valueOf(
                                                            1000),
                                                    6,
                                                    RoundingMode.HALF_UP))
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add);

            BigDecimal netRevenue =
                    grossRevenue.subtract(
                            refundedAmount);

            BigDecimal profit =
                    netRevenue.subtract(
                            totalCost);

            RevenueReportResponse response =
                    RevenueReportResponse.builder()
                            .totalOrders(
                                    totalOrders)
                            .grossRevenue(
                                    grossRevenue.setScale(
                                            2,
                                            RoundingMode.HALF_UP))
                            .refundedAmount(
                                    refundedAmount.setScale(
                                            2,
                                            RoundingMode.HALF_UP))
                            .netRevenue(
                                    netRevenue.setScale(
                                            2,
                                            RoundingMode.HALF_UP))
                            .totalCost(
                                    totalCost.setScale(
                                            2,
                                            RoundingMode.HALF_UP))
                            .profit(
                                    profit.setScale(
                                            2,
                                            RoundingMode.HALF_UP))
                            .build();

            responseData.setData(
                    response);

        } catch (Exception e) {

            LOGGER.error(
                    logPrefix + e.getMessage(),
                    e);

            responseData.setCode(500);
            responseData.setMessage(
                    "Internal Server Error");
        }

        return responseData;
    }

    @Override
    public ResponseData<?> serviceReport(RevenueReportRequest request) {

        String logPrefix =
                "[ReportServiceImpl][serviceReport] - ";

        ResponseData<List<ServiceRevenueResponse>>
                responseData = new ResponseData<>();

        try {

            LocalDateTime from =
                    request.getFromDate()
                            .atStartOfDay();

            LocalDateTime to =
                    request.getToDate()
                            .atTime(
                                    23,
                                    59,
                                    59);

            List<OrderEntity> orders =
                    orderRepository.findByCreatedDateBetween(
                            from,
                            to);

            Map<Long, List<OrderEntity>> grouped =
                    orders.stream()
                            .collect(
                                    Collectors.groupingBy(
                                            order ->
                                                    order.getPanelService()
                                                            .getId()));

            List<ServiceRevenueResponse> result =
                    new ArrayList<>();

            for (Map.Entry<Long,
                    List<OrderEntity>> entry
                    : grouped.entrySet()) {

                List<OrderEntity> serviceOrders =
                        entry.getValue();

                OrderEntity first =
                        serviceOrders.get(0);

                BigDecimal revenue =
                        serviceOrders.stream()
                                .map(
                                        OrderEntity::getAmount)
                                .reduce(
                                        BigDecimal.ZERO,
                                        BigDecimal::add);

                BigDecimal cost =
                        serviceOrders.stream()
                                .map(order ->
                                        order.getCostPrice()
                                                .multiply(
                                                        BigDecimal.valueOf(
                                                                order.getQuantity()))
                                                .divide(
                                                        BigDecimal.valueOf(
                                                                1000),
                                                        6,
                                                        RoundingMode.HALF_UP))
                                .reduce(
                                        BigDecimal.ZERO,
                                        BigDecimal::add);

                BigDecimal profit =
                        revenue.subtract(
                                cost);

                Long totalQuantity =
                        serviceOrders.stream()
                                .mapToLong(
                                        OrderEntity::getQuantity)
                                .sum();

                BigDecimal avgSellPrice =
                        serviceOrders.stream()
                                .map(OrderEntity::getSellPrice)
                                .reduce(
                                        BigDecimal.ZERO,
                                        BigDecimal::add)
                                .divide(
                                        BigDecimal.valueOf(
                                                serviceOrders.size()),
                                        2,
                                        RoundingMode.HALF_UP);
                BigDecimal avgCostPrice =
                        serviceOrders.stream()
                                .map(OrderEntity::getCostPrice)
                                .reduce(
                                        BigDecimal.ZERO,
                                        BigDecimal::add)
                                .divide(
                                        BigDecimal.valueOf(
                                                serviceOrders.size()),
                                        2,
                                        RoundingMode.HALF_UP);

                result.add(
                        ServiceRevenueResponse.builder()
                                .panelServiceId(
                                        first.getPanelService()
                                                .getId())
                                .panelServiceName(
                                        first.getPanelService()
                                                .getName())
                                .totalOrders(
                                        (long) serviceOrders.size())
                                .totalQuantity(
                                        totalQuantity)
                                .revenue(
                                        revenue.setScale(
                                                2,
                                                RoundingMode.HALF_UP))
                                .cost(
                                        cost.setScale(
                                                2,
                                                RoundingMode.HALF_UP))
                                .profit(
                                        profit.setScale(
                                                2,
                                                RoundingMode.HALF_UP))
                                .avgSellPrice(
                                        avgSellPrice)
                                .avgCostPrice(
                                        avgCostPrice)
                                .build());
            }

            result.sort(
                    Comparator.comparing(
                                    ServiceRevenueResponse::getRevenue)
                            .reversed());

            responseData.setData(
                    result);

        } catch (Exception e) {

            LOGGER.error(
                    logPrefix + e.getMessage(),
                    e);

            responseData.setCode(500);
            responseData.setMessage(
                    "Internal Server Error");
        }

        return responseData;
    }
}
