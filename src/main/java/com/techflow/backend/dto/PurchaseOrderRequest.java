package com.techflow.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderRequest {

    @NotBlank(message = "La dirección de envío es obligatoria")
    private String shippingAddress;

    private String paymentMethod; // EFECTIVO, TARJETA, TRANSFERENCIA

    private String notes;

    @NotEmpty(message = "Debe agregar al menos un producto")
    private List<PurchaseItemRequest> items;

    @Data
    public static class PurchaseItemRequest {
        @NotNull(message = "El SKU del producto es obligatorio")
        private String productSku;

        @NotNull(message = "La cantidad es obligatoria")
        private Integer quantity;
    }
}
