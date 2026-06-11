package com.example.smm_cms.repository;

import com.example.smm_cms.entity.OrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistoryEntity, Long> {
    List<OrderHistoryEntity> findByOrderIdOrderByCreatedDateDesc(Long orderId);
}
