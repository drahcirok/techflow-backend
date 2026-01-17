package com.techflow.backend.repository;

import com.techflow.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Buscar por código de barras
    Optional<Product> findBySku(String sku);

    // Para evitar registrar dos productos con el mismo código
    boolean existsBySku(String sku);

    // Consulta personalizada JPQL para productos con stock bajo
    // "Dame los productos donde el stock sea menor o igual a su umbral de alerta"
    @org.springframework.data.jpa.repository.Query("SELECT p FROM Product p WHERE p.stock <= p.lowStockThreshold")
    List<Product> findLowStockProducts();
}