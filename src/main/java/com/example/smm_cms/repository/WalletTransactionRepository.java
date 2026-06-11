package com.example.smm_cms.repository;

import com.example.smm_cms.entity.WalletTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, Long>, JpaSpecificationExecutor<WalletTransactionEntity> {
    List<WalletTransactionEntity> findByReferenceIdOrderByCreatedDateDesc(Long referenceId);

    @Query("""
            SELECT COALESCE(SUM(w.amount),0)
            FROM WalletTransactionEntity w
            WHERE w.type = 'REFUND'
            AND w.createdDate BETWEEN :from AND :to
            """)
    BigDecimal sumRefund(
            LocalDateTime from,
            LocalDateTime to);
}
