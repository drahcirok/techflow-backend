package com.techflow.backend.enums;

public enum OrderStatus {
    PENDIENTE,          // Recién llegado
    DIAGNOSTICO,        // Técnico revisando
    EN_ESPERA_REPUESTO, // Falta una pieza
    REPARADO,           // Listo para entregar
    ENTREGADO,          // Cliente se lo llevó
    CANCELADO           // Cliente no aceptó presupuesto
}
