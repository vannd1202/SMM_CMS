package com.example.smm_cms.repository;

import com.example.smm_cms.common.OrderStatus;
import com.example.smm_cms.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    List<OrderEntity> findByStatusIn(
            Collection<OrderStatus> statuses);

    Long countByStatus(
            OrderStatus status);

    Long countByRefundedTrue();

    @Query("""
                SELECT COALESCE(SUM(o.amount),0)
                FROM OrderEntity o
                WHERE o.status IN (
                    com.example.smm_cms.common.OrderStatus.COMPLETED,
                    com.example.smm_cms.common.OrderStatus.PARTIAL,
                    com.example.smm_cms.common.OrderStatus.PROCESSING,
                    com.example.smm_cms.common.OrderStatus.PENDING
                )
            """)
    BigDecimal sumRevenue();

    @Query("""
                SELECT COALESCE(
                    SUM(
                        (o.costPrice * o.quantity) / 1000
                    ),
                    0
                )
                FROM OrderEntity o
                WHERE o.status IN (
                    com.example.smm_cms.common.OrderStatus.COMPLETED,
                    com.example.smm_cms.common.OrderStatus.PARTIAL,
                    com.example.smm_cms.common.OrderStatus.PROCESSING,
                    com.example.smm_cms.common.OrderStatus.PENDING
                )
            """)
    BigDecimal sumCost();

    @Query("""
            SELECT COALESCE(COUNT(o),0)
            FROM OrderEntity o
            WHERE o.createdDate BETWEEN :from AND :to
            """)
    Long countOrders(LocalDateTime from, LocalDateTime to);

    @Query("""
            SELECT COALESCE(SUM(o.amount),0)
            FROM OrderEntity o
            WHERE o.createdDate BETWEEN :from AND :to
            """)
    BigDecimal sumRevenue(LocalDateTime from, LocalDateTime to);

    List<OrderEntity> findByCreatedDateBetween(
            LocalDateTime from,
            LocalDateTime to);


    @Query("""
    SELECT COUNT(o)
    FROM OrderEntity o
    WHERE o.createdDate BETWEEN :from AND :to
""")
    Long countOrdersReport(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
    SELECT COALESCE(SUM(o.amount), 0)
    FROM OrderEntity o
    WHERE o.createdDate BETWEEN :from AND :to
""")
    BigDecimal sumRevenueReport(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    Long countByMappingProviderServiceProviderId(
            Long providerId);

    Long countByMappingProviderServiceProviderIdAndStatus(
            Long providerId,
            OrderStatus status);
}
