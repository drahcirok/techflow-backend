package com.techflow.backend.entity;

import com.techflow.backend.enums.OrderStatus;
import com.techflow.backend.enums.ServiceType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.security.SecureRandom; // 👈 Necesario para el generador aleatorio
import java.time.LocalDateTime;
import java.util.List;

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

    // Código corto y profesional (Ej: TF-A1B2C3)
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

    // 💰 CAMPOS DE DINERO
    @Column(name = "labor_cost")
    private BigDecimal laborCost;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    // DATOS DEL CLIENTE (para órdenes sin cuenta o para vincular después)
    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_phone")
    private String clientPhone;

    // RELACIONES
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client; // Puede ser null si el cliente no tiene cuenta

    @ManyToOne
    @JoinColumn(name = "technician_id")
    private User technician;

    // 📋 LISTA DE REPUESTOS
    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<OrderItem> items;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ⭐ CAMPOS DE VALORACIÓN DEL CLIENTE
    @Column(name = "rating")
    private Integer rating; // 1-5 estrellas

    @Column(name = "rating_comment", columnDefinition = "TEXT")
    private String ratingComment;

    @Column(name = "rated_at")
    private LocalDateTime ratedAt;

    // 👇 AQUÍ OCURRE LA MAGIA AUTOMÁTICA
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        // Generamos el código corto si no existe
        if (this.trackingCode == null) {
            // Resultado: "TF-" + 6 caracteres aleatorios (Ej: TF-4X9A2Z)
            this.trackingCode = "TF-" + generateRandomString(6);
        }

        // Inicializamos costos en 0 si vienen nulos
        if (this.laborCost == null) this.laborCost = BigDecimal.ZERO;
        if (this.totalCost == null) this.totalCost = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 👇 MÉTODO PRIVADO PARA GENERAR LETRAS Y NÚMEROS
    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}