package com.techflow.backend.repository;

import com.techflow.backend.entity.PurchaseOrder;
import com.techflow.backend.enums.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);

    List<PurchaseOrder> findByClientIdOrderByCreatedAtDesc(Long clientId);

    List<PurchaseOrder> findByClientEmailOrderByCreatedAtDesc(String clientEmail);

    List<PurchaseOrder> findByStatusOrderByCreatedAtDesc(PurchaseStatus status);

    List<PurchaseOrder> findAllByOrderByCreatedAtDesc();
}
