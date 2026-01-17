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

    @Transactional // 👈 IMPORTANTE: Si algo falla (como falta de stock), se deshace todo.
    public ServiceOrder createOrder(ServiceOrderRequest request) {

        // 1. Buscar al cliente
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // 2. Definir Mano de Obra (Si no envían nada, asumimos 0 para evitar errores matemáticos)
        BigDecimal laborCost = request.getLaborCost() != null ? request.getLaborCost() : BigDecimal.ZERO;

        // 3. Crear la estructura base de la Orden
        ServiceOrder order = ServiceOrder.builder()
                .description(request.getDescription())
                .type(request.getType())
                .status(OrderStatus.PENDIENTE) // Nace como Pendiente
                .client(client)
                .laborCost(laborCost) // Guardamos el costo de mano de obra
                .items(new ArrayList<>()) // Inicializamos la lista vacía para llenarla abajo
                .build();

        BigDecimal itemsTotalCost = BigDecimal.ZERO; // Acumulador para sumar los repuestos

        // 4. Procesar los repuestos (si los hay)
        if (request.getItems() != null && !request.getItems().isEmpty()) {

            for (OrderItemRequest itemRequest : request.getItems()) {
                // a. Buscar producto
                Product product = productRepository.findBySku(itemRequest.getProductSku())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemRequest.getProductSku()));

                // b. Validar Stock
                if (product.getStock() < itemRequest.getQuantity()) {
                    throw new RuntimeException("Stock insuficiente para: " + product.getName());
                }

                // c. Descontar del Inventario
                product.setStock(product.getStock() - itemRequest.getQuantity());
                productRepository.save(product);

                // d. Calcular costo de este ítem (Precio actual * Cantidad)
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                itemsTotalCost = itemsTotalCost.add(itemTotal);

                // e. Crear el item de la orden y vincularlo
                OrderItem orderItem = OrderItem.builder()
                        .serviceOrder(order) // Vinculamos a la orden padre
                        .product(product)
                        .quantity(itemRequest.getQuantity())
                        .price(product.getPrice()) // Congelamos el precio al momento de la orden
                        .build();

                // f. Agregar a la lista de la orden (Importante para que se guarden juntos)
                order.getItems().add(orderItem);
            }
        }

        // 5. Calcular Total Final (Mano de Obra + Repuestos)
        order.setTotalCost(laborCost.add(itemsTotalCost));

        // 6. Guardar todo
        // Gracias a CascadeType.ALL en la entidad, al guardar la orden se guardan los items automáticamente
        return orderRepository.save(order);
    }

    // Método para que el cliente rastree su orden
    public ServiceOrder getOrderByTrackingCode(String trackingCode) {
        return orderRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con código: " + trackingCode));
    }

    // Método auxiliar para buscar por ID (útil para imprimir factura si estás logueado)
    public ServiceOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }
    // ... código anterior ...

    // Método para cambiar el estado (Ej: DE PENDIENTE A TERMINADO)
    public ServiceOrder updateStatus(Long id, OrderStatus newStatus) {
        ServiceOrder order = getOrderById(id); // Reusamos el método que ya tienes
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}
