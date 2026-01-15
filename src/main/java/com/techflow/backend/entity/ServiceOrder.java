package com.techflow.backend.entity;

import com.techflow.backend.enums.OrderStatus;
import com.techflow.backend.enums.ServiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "service_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código de seguimiento para el cliente (Ej: "550e8400-e29b...")
    // Usamos UUID para que sea difícil de adivinar por otros
    @Column(name = "tracking_code", unique = true, nullable = false, updatable = false)
    private String trackingCode;

    @Column(nullable = false)
    private String description; // Lo que el cliente dice que falla

    @Column(name = "technician_note", columnDefinition = "TEXT")
    private String technicianNote; // Diagnóstico técnico

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType type;

    // RELACIONES:

    // Muchos pedidos pueden ser de un solo cliente
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    // Muchos pedidos pueden ser atendidos por un solo técnico
    @ManyToOne
    @JoinColumn(name = "technician_id")
    private User technician; // Puede ser null al principio si nadie la ha tomado

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Auditoría automática antes de guardar
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.trackingCode == null) {
            this.trackingCode = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}