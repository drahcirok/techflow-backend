package com.techflow.backend.dto;

import lombok.Data;

@Data
public class OrderItemRequest {
    private String productSku; // Buscamos por código, no por ID (es más real)
    private Integer quantity;
}