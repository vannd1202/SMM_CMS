package com.example.smm_cms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_history")
@Getter
@Setter
public class OrderHistoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private OrderEntity order;

    private String oldStatus;

    private String newStatus;

    @Column(length = 1000)
    private String note;
}
