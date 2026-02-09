package com.techflow.backend.repository;

import com.techflow.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Buscar por código de barras
    Optional<Product> findBySku(String sku);

    // Para evitar registrar dos productos con el mismo código
    boolean existsBySku(String sku);

    // 👇 1. NUEVO: Método vital para el "Borrado Lógico"
    // Solo traerá los productos donde active = true
    List<Product> findByActiveTrue();

    // 👇 2. MEJORA: Solo buscar stock bajo en productos ACTIVOS
    // (Así no recibes alertas de productos que ya "borraste")
    @Query("SELECT p FROM Product p WHERE p.stock <= p.lowStockThreshold AND p.active = true")
    List<Product> findLowStockProducts();

    // 👇 3. Para la tienda: productos disponibles (activos y con stock)
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stock > 0 ORDER BY p.name ASC")
    List<Product> findAvailableProducts();

}