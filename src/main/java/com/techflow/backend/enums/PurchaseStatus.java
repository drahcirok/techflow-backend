package com.techflow.backend.enums;

public enum PurchaseStatus {
    PENDIENTE,      // Orden creada, esperando pago/procesamiento
    PROCESANDO,     // Preparando el pedido
    ENVIADO,        // Pedido en camino
    ENTREGADO,      // Pedido entregado
    CANCELADO       // Orden cancelada
}
