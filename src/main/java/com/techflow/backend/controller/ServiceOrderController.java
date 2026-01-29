package com.techflow.backend.controller;

import com.techflow.backend.dto.ServiceOrderRequest;
import com.techflow.backend.entity.ServiceOrder;
import com.techflow.backend.service.ServiceOrderService;
import com.techflow.backend.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    // 🟢 GET PRINCIPAL: Solo trae las órdenes ACTIVAS
    // Cuando entres al Dashboard, llamarás a este.
    @GetMapping
    public ResponseEntity<List<ServiceOrder>> getAllOrders() {
        return ResponseEntity.ok(serviceOrderService.getAllActiveOrders());
    }

    // 🟠 GET HISTORIAL: Solo trae las órdenes CERRADAS
    // Cuando entres a la página de "Historial", llamarás a este.
    @GetMapping("/history")
    public ResponseEntity<List<ServiceOrder>> getHistory() {
        return ResponseEntity.ok(serviceOrderService.getHistoryOrders());
    }

    @PostMapping
    public ResponseEntity<ServiceOrder> createOrder(@RequestBody ServiceOrderRequest request) {
        return ResponseEntity.ok(serviceOrderService.createOrder(request));
    }

    @GetMapping("/track/{trackingCode}")
    public ResponseEntity<ServiceOrder> trackOrder(@PathVariable String trackingCode) {
        return ResponseEntity.ok(serviceOrderService.getOrderByTrackingCode(trackingCode));
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<ServiceOrder> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(serviceOrderService.getOrderById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ServiceOrder> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(serviceOrderService.updateStatus(id, status));
    }
}