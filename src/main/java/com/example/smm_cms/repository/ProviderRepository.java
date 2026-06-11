package com.example.smm_cms.repository;

import com.example.smm_cms.entity.ProviderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProviderRepository extends JpaRepository<ProviderEntity, Long> {

    Page<ProviderEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
