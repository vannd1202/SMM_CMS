package com.example.smm_cms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "service_mapping")
@Getter
@Setter
public class ServiceMappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PanelServiceEntity panelService;

    @ManyToOne
    private ServiceEntity providerService;

    private Integer priority;

}
