package com.techflow.backend.controller;

import com.techflow.backend.dto.ServiceOrderRequest;
import com.techflow.backend.entity.ServiceOrder;
import com.techflow.backend.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.techflow.backend.enums.OrderStatus;

@RestController
@RequestMapping("/api/orders") // 🔐 Ruta protegida
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    @PostMapping
    public ResponseEntity<ServiceOrder> createOrder(@RequestBody ServiceOrderRequest request) {
        return ResponseEntity.ok(serviceOrderService.createOrder(request));
    }
    // ... código anterior ...

    // Endpoint: GET /api/orders/track/550e8400-e29b...
    @GetMapping("/track/{trackingCode}")
    public ResponseEntity<ServiceOrder> trackOrder(@PathVariable String trackingCode) {
        return ResponseEntity.ok(serviceOrderService.getOrderByTrackingCode(trackingCode));
    }
    // GET /api/orders/{id}/invoice
    @GetMapping("/{id}/invoice")
    public ResponseEntity<ServiceOrder> getInvoice(@PathVariable Long id) {
        // 1. Buscas la orden
        ServiceOrder order = serviceOrderService.getOrderById(id);

        // 2. Seguridad: ¿El usuario que pide esto es el dueño de la orden?
        // (Aquí usas el token para verificar que el ID del cliente coincida)

        return ResponseEntity.ok(order);
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<ServiceOrder> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(serviceOrderService.updateStatus(id, status));
    }

}
