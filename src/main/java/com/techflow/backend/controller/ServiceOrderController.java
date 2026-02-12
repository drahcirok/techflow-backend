package com.techflow.backend.controller;

import com.techflow.backend.dto.RatingRequest;
import com.techflow.backend.dto.ServiceOrderRequest;
import com.techflow.backend.entity.ServiceOrder;
import com.techflow.backend.entity.User;
import com.techflow.backend.service.ServiceOrderService;
import com.techflow.backend.enums.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // 🔵 GET ÓRDENES DEL CLIENTE AUTENTICADO (busca por ID y por email para vincular órdenes anteriores)
    @GetMapping("/mine")
    public ResponseEntity<List<ServiceOrder>> getMyOrders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(serviceOrderService.getOrdersByClientIdOrEmail(user.getId(), user.getEmail()));
    }

    // 🔵 GET ORDEN POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrder> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceOrderService.getOrderById(id));
    }

    // ⭐ POST VALORACIÓN DE UNA ORDEN
    @PostMapping("/{id}/rating")
    public ResponseEntity<ServiceOrder> addRating(
            @PathVariable Long id,
            @Valid @RequestBody RatingRequest request,
            @AuthenticationPrincipal User user) {
        // Verificar que el usuario es el dueño de la orden
        ServiceOrder order = serviceOrderService.getOrderById(id);

        // Verificar por clientId O por clientEmail
        boolean isOwner = false;
        if (order.getClient() != null && order.getClient().getId().equals(user.getId())) {
            isOwner = true;
        } else if (order.getClientEmail() != null && order.getClientEmail().equalsIgnoreCase(user.getEmail())) {
            isOwner = true;
        }

        if (!isOwner) {
            throw new RuntimeException("No tienes permiso para valorar esta orden");
        }
        return ResponseEntity.ok(serviceOrderService.addRating(id, request.getRating(), request.getComment()));
    }

    // 🔵 GET ÓRDENES DE UN CLIENTE ESPECÍFICO (para admin)
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ServiceOrder>> getOrdersByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(serviceOrderService.getOrdersByClientId(clientId));
    }

    // 🔵 GET TODAS LAS ÓRDENES (incluyendo historial) - para admin
    @GetMapping("/all")
    public ResponseEntity<List<ServiceOrder>> getAllOrdersIncludingHistory() {
        return ResponseEntity.ok(serviceOrderService.getAllOrders());
    }
}