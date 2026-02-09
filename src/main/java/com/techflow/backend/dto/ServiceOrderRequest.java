package com.techflow.backend.dto;

import com.techflow.backend.enums.ServiceType;
import lombok.Data;
import java.math.BigDecimal; // 👈 Importar
import java.util.List;

@Data
public class ServiceOrderRequest {
    private String description;
    private ServiceType type;
    private Long clientId; // ID del cliente si ya tiene cuenta
    private String clientEmail; // Email del cliente (para vincular después)
    private String clientName; // Nombre del cliente sin cuenta
    private String clientPhone; // Teléfono del cliente sin cuenta
    private BigDecimal laborCost;
    private List<OrderItemRequest> items;
}