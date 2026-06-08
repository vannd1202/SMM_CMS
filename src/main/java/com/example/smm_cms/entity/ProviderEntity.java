package com.example.smm_cms.entity;

import com.example.smm_cms.common.ProviderStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "api_url", nullable = false)
    private String apiUrl;

    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Enumerated(EnumType.STRING)
    private ProviderStatus status;

}
