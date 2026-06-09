package com.example.smm_cms.repository;

import com.example.smm_cms.entity.ServiceMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceMappingRepository extends JpaRepository<ServiceMappingEntity, Long> {
    List<ServiceMappingEntity> findByPanelServiceIdOrderByPriorityAsc(Long panelServiceId);

    Optional<ServiceMappingEntity> findFirstByPanelServiceIdOrderByPriorityAsc(Long panelServiceId);
}
