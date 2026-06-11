package com.example.smm_cms.repository;

import com.example.smm_cms.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long>, JpaSpecificationExecutor<ServiceEntity> {
    Optional<ServiceEntity> findByProviderIdAndProviderServiceId(Long providerId, Long providerServiceId);
    List<ServiceEntity> findByProviderId(Long providerId);
    Integer countByProviderId(Long providerId);

}
