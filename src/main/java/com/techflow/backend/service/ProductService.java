package com.techflow.backend.service;

import com.techflow.backend.dto.ProductDTO;
import com.techflow.backend.entity.Product;
import com.techflow.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // Crear producto nuevo
    public Product createProduct(ProductDTO dto) {
        if (productRepository.existsBySku(dto.getSku())) {
            throw new RuntimeException("El producto con SKU " + dto.getSku() + " ya existe");
        }

        Product product = Product.builder()
                .sku(dto.getSku())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .lowStockThreshold(dto.getLowStockThreshold())
                .build();

        return productRepository.save(product);
    }

    // Listar todos
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}