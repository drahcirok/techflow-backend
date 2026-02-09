package com.techflow.backend.controller;

import com.techflow.backend.dto.PurchaseOrderRequest;
import com.techflow.backend.entity.PurchaseOrder;
import com.techflow.backend.entity.User;
import com.techflow.backend.enums.PurchaseStatus;
import com.techflow.backend.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    // 🟢 POST - Crear una orden de compra
    @PostMapping
    public ResponseEntity<PurchaseOrder> createPurchase(
            @Valid @RequestBody PurchaseOrderRequest request,
            @AuthenticationPrincipal User user) {
        PurchaseOrder order = purchaseOrderService.createPurchaseOrder(request, user);
        return ResponseEntity.ok(order);
    }

    // 🟢 GET - Obtener mis órdenes de compra
    @GetMapping("/mine")
    public ResponseEntity<List<PurchaseOrder>> getMyPurchases(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(purchaseOrderService.getMyPurchaseOrders(user));
    }

    // 🟢 GET - Obtener orden por ID
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getPurchaseById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderById(id));
    }

    // 🟢 GET - Obtener orden por número
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<PurchaseOrder> getPurchaseByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderByNumber(orderNumber));
    }

    // 🟠 PATCH - Actualizar estado (admin/técnico)
    @PatchMapping("/{id}/status")
    public ResponseEntity<PurchaseOrder> updateStatus(
            @PathVariable Long id,
            @RequestParam PurchaseStatus status) {
        return ResponseEntity.ok(purchaseOrderService.updateStatus(id, status));
    }

    // 🟠 GET - Todas las órdenes (admin)
    @GetMapping("/all")
    public ResponseEntity<List<PurchaseOrder>> getAllPurchases() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    // 🟠 GET - Órdenes por estado (admin)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseOrder>> getPurchasesByStatus(@PathVariable PurchaseStatus status) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrdersByStatus(status));
    }
}
