package com.techflow.backend.controller;

import com.techflow.backend.dto.ServiceOrderRequest;
import com.techflow.backend.entity.ServiceOrder;
import com.techflow.backend.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders") // 🔐 Ruta protegida
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    @PostMapping
    public ResponseEntity<ServiceOrder> createOrder(@RequestBody ServiceOrderRequest request) {
        return ResponseEntity.ok(serviceOrderService.createOrder(request));
    }
}