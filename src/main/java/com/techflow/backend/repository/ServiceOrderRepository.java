package com.techflow.backend.repository;

import com.techflow.backend.entity.ServiceOrder;
import com.techflow.backend.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    Optional<ServiceOrder> findByTrackingCode(String trackingCode);

    // 1. Para el Historial (Ya entregados o cancelados)
    List<ServiceOrder> findByStatusInOrderByCreatedAtDesc(List<OrderStatus> statuses);

    // 2. Para el Dashboard Activo (Todo lo que NO esté entregado ni cancelado)
    // 👇 Esto es lo que hace que desaparezcan de la vista principal
    List<ServiceOrder> findByStatusNotInOrderByCreatedAtDesc(List<OrderStatus> statuses);

    // 3. Para obtener órdenes de un cliente específico
    List<ServiceOrder> findByClientIdOrderByCreatedAtDesc(Long clientId);

    // 4. Para obtener órdenes por email (para vincular órdenes anteriores a cuenta nueva)
    List<ServiceOrder> findByClientEmailOrderByCreatedAtDesc(String clientEmail);

    // 5. Para obtener órdenes por cliente ID o por email
    List<ServiceOrder> findByClientIdOrClientEmailOrderByCreatedAtDesc(Long clientId, String clientEmail);

    // 6. Para obtener todas las órdenes ordenadas por fecha
    List<ServiceOrder> findAllByOrderByCreatedAtDesc();
}