package com.techflow.backend.service;

import com.techflow.backend.dto.OrderItemRequest;
import com.techflow.backend.dto.ServiceOrderRequest;
import com.techflow.backend.entity.*;
import com.techflow.backend.enums.OrderStatus;
import com.techflow.backend.repository.ProductRepository;
import com.techflow.backend.repository.ServiceOrderRepository;
import com.techflow.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional // 👈 IMPORTANTE: Si algo falla, se deshacen todos los cambios (Rollback)
    public ServiceOrder createOrder(ServiceOrderRequest request) {

        // 1. Buscar al cliente
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // 2. Crear la cabecera de la orden
        ServiceOrder order = ServiceOrder.builder()
                .description(request.getDescription())
                .type(request.getType())
                .status(OrderStatus.PENDIENTE) // Nace como Pendiente
                .client(client)
                .build();

        // 3. Procesar los repuestos (si los hay)
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<OrderItem> orderItems = new ArrayList<>();

            for (OrderItemRequest itemRequest : request.getItems()) {
                // a. Buscar producto
                Product product = productRepository.findBySku(itemRequest.getProductSku())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemRequest.getProductSku()));

                // b. Validar Stock
                if (product.getStock() < itemRequest.getQuantity()) {
                    throw new RuntimeException("Stock insuficiente para: " + product.getName());
                }

                // c. Descontar del Inventario (¡Magia!)
                product.setStock(product.getStock() - itemRequest.getQuantity());
                productRepository.save(product);

                // d. Crear el item de la orden
                OrderItem orderItem = OrderItem.builder()
                        .serviceOrder(order)
                        .product(product)
                        .quantity(itemRequest.getQuantity())
                        .price(product.getPrice()) // Congelamos el precio al momento de la orden
                        .build();

                orderItems.add(orderItem);
            }
            // Aquí deberíamos guardar los items, pero por simplicidad en JPA
            // a veces se configuran cascadas. Por ahora dejémoslo así.
        }

        // 4. Guardar y devolver
        return orderRepository.save(order);
    }
}