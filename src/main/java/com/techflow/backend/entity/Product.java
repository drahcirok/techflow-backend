package com.techflow.backend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true; // 👈 Por defecto, todo nace activo

    @Column(unique = true, nullable = false)
    private String sku; // Código de barras o identificador único (Ej: RAM-COR-8GB)

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT") // Permite descripciones largas
    private String description;

    @Column(nullable = false)
    private BigDecimal price; // Siempre usa BigDecimal para dinero

    @Column(nullable = false)
    private Integer stock; // Cantidad actual

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold; // Alerta: "Avísame si baja de 5 unidades"
}