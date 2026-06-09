package com.example.smm_cms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "service",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_provider_service",
                        columnNames = {
                                "provider_id",
                                "provider_service_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @Column(name = "provider_service_id", nullable = false)
    private Long providerServiceId;

    private String name;

    private String category;

    private String type;

    @Column(precision = 18, scale = 6)
    private BigDecimal rate;

    private Integer minQuantity;

    private Integer maxQuantity;

    private Boolean refill;

    private Boolean cancelable;

    private Boolean active;

}
