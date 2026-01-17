package com.techflow.backend.dto;

import com.techflow.backend.enums.ServiceType;
import lombok.Data;
import java.math.BigDecimal; // 👈 Importar
import java.util.List;

@Data
public class ServiceOrderRequest {
    private String description;
    private ServiceType type;
    private Long clientId;
    private BigDecimal laborCost; // 👈 Nuevo campo: "Cuánto cobro por el trabajo"
    private List<OrderItemRequest> items;
}