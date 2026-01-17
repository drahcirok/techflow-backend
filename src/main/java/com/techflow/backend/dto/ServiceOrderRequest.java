package com.techflow.backend.dto;

import com.techflow.backend.enums.ServiceType;
import lombok.Data;
import java.util.List;

@Data
public class ServiceOrderRequest {
    private String description;      // "Pantalla azul y hace ruido raro"
    private ServiceType type;        // REPARACION, MANTENIMIENTO, ETC
    private Long clientId;           // ¿De quién es la compu?
    private List<OrderItemRequest> items; // (Opcional) Repuestos usados al recibirla
}