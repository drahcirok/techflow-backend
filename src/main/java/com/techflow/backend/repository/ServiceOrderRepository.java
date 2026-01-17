package com.techflow.backend.repository;

import com.techflow.backend.entity.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    // Para que el cliente busque su orden con el código UUID (Ej: "550e8...")
    Optional<ServiceOrder> findByTrackingCode(String trackingCode);
}