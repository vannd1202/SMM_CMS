package com.example.smm_cms.repository;

import com.example.smm_cms.common.OrderStatus;
import com.example.smm_cms.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrderRepository  extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    List<OrderEntity> findByStatusIn(
            Collection<OrderStatus> statuses);
//    @Query("""
//            SELECT o
//            FROM OrderEntity o
//            WHERE o.status IN :statuses
//            ORDER BY o.updatedDate ASC
//            """)
//    Page<OrderEntity> findOrdersNeedSync(
//            Collection<OrderStatus> statuses,
//            Pageable pageable);


}
