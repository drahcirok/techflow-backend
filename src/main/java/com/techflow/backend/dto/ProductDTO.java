package com.techflow.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String sku;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private Integer stock;
    private Integer lowStockThreshold;
}