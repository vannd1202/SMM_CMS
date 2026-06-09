package com.example.smm_cms.repository;

import com.example.smm_cms.entity.PanelServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PanelRepository extends JpaRepository<PanelServiceEntity, Long> {
    Page<PanelServiceEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
