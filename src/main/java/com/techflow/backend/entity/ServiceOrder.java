package com.techflow.backend.entity;

import com.techflow.backend.enums.OrderStatus;
import com.techflow.backend.enums.ServiceType;
import com.fasterxml.jackson.annotation.JsonManagedReference; // 👈 Importante para evitar bucles infinitos
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "tracking_code", unique = true, nullable = false, updatable = false)
    private String trackingCode;

    @Column(nullable = false)
    private String description;

    @Column(name = "technician_note", columnDefinition = "TEXT")
    private String technicianNote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType type;

    // 💰 NUEVOS CAMPOS DE DINERO
    @Column(name = "labor_cost")
    private BigDecimal laborCost; // Lo que cobras por tu mano de obra

    @Column(name = "total_cost")
    private BigDecimal totalCost; // La suma final (Repuestos + Mano de obra)

    // RELACIONES
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne
    @JoinColumn(name = "technician_id")
    private User technician;

    // 📋 LISTA DE REPUESTOS (Para que salgan en la factura)
    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference // 👈 Ayuda a que Java convierta esto a JSON sin errores
    private List<OrderItem> items;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.trackingCode == null) {
            this.trackingCode = UUID.randomUUID().toString();
        }
        // Inicializamos costos en 0 si vienen nulos para evitar errores matemáticos
        if (this.laborCost == null) this.laborCost = BigDecimal.ZERO;
        if (this.totalCost == null) this.totalCost = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}