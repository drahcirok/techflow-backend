package com.techflow.backend.controller;

import com.techflow.backend.dto.ServiceOrderRequest;
import com.techflow.backend.entity.ServiceOrder;
import com.techflow.backend.service.ServiceOrderService;
import com.techflow.backend.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // 👈 Importante para las listas

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    // 👇 ESTE ES EL MÉTODO QUE TE FALTABA 👇
    @GetMapping
    public ResponseEntity<List<ServiceOrder>> getAllOrders() {
        return ResponseEntity.ok(serviceOrderService.getAllOrders());
    }
    // 👆 SIN ESTO, EL DASHBOARD NO PUEDE MOSTRAR LA TABLA

    @PostMapping
    public ResponseEntity<ServiceOrder> createOrder(@RequestBody ServiceOrderRequest request) {
        return ResponseEntity.ok(serviceOrderService.createOrder(request));
    }

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
        // Aquí podrías validar si el usuario es dueño de la orden
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ServiceOrder> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(serviceOrderService.updateStatus(id, status));
    }
}