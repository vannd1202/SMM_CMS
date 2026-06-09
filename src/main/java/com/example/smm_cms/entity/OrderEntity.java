package com.example.smm_cms.entity;

import com.example.smm_cms.common.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panel_service_id")
    private PanelServiceEntity panelService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapping_id")
    private ServiceMappingEntity mapping;

    private String providerOrderId;

    private String target;

    private Integer quantity;

    @Column(precision = 18, scale = 6)
    private BigDecimal costPrice;

    @Column(precision = 18, scale = 6)
    private BigDecimal sellPrice;

    @Column(precision = 18, scale = 6)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Integer startCount;

    private Integer remains;

    private String providerStatus;

    private LocalDateTime lastSyncTime;

    private Integer syncCount;


}
