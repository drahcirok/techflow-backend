package com.techflow.backend.service;

import com.techflow.backend.dto.ProductDTO;
import com.techflow.backend.entity.Product;
import com.techflow.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 1. CREAR PRODUCTO (Con lógica de resurrección 🧟‍♂️)
    public Product createProduct(ProductDTO dto) {
        // Buscamos si ya existe el SKU (incluso si está "borrado")
        Optional<Product> existingProduct = productRepository.findBySku(dto.getSku());

        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();

            // CASO A: Si existe y ESTÁ ACTIVO -> Error real (No queremos duplicados)
            if (Boolean.TRUE.equals(product.getActive())) {
                throw new RuntimeException("El producto con SKU " + dto.getSku() + " ya existe y está activo.");
            }

            // CASO B: Si existe pero ESTÁ INACTIVO -> ¡Lo Resucitamos! ✨
            // Actualizamos los datos viejos con los nuevos que enviaste
            product.setActive(true); // ¡Vuelve a la vida!
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setImageUrl(dto.getImageUrl());
            product.setPrice(dto.getPrice());
            product.setStock(dto.getStock());
            product.setLowStockThreshold(dto.getLowStockThreshold());

            return productRepository.save(product);
        }

        // CASO C: Si no existe de ninguna forma, creamos uno nuevo desde cero
        Product newProduct = Product.builder()
                .sku(dto.getSku())
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .lowStockThreshold(dto.getLowStockThreshold())
                .active(true) // Nos aseguramos que nazca activo
                .build();

        return productRepository.save(newProduct);
    }

    // 2. LISTAR TODOS (Solo traemos los activos)
    public List<Product> getAllProducts() {
        return productRepository.findByActiveTrue();
    }

    // 3. BUSCAR POR ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // 4. ACTUALIZAR PRODUCTO
    public Product updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setImageUrl(dto.getImageUrl());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setLowStockThreshold(dto.getLowStockThreshold());

        return productRepository.save(product);
    }

    // 5. BORRAR PRODUCTO (Borrado Lógico / Soft Delete)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        product.setActive(false); // 👻 Lo ocultamos en lugar de borrarlo
        productRepository.save(product);
    }

    // 6. PRODUCTOS DISPONIBLES PARA LA TIENDA (activos y con stock > 0)
    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }
}