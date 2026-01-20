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

    @Transactional
    public ServiceOrder createOrder(ServiceOrderRequest request) {

        // 1. Buscar al cliente
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // 2. Definir Mano de Obra
        BigDecimal laborCost = request.getLaborCost() != null ? request.getLaborCost() : BigDecimal.ZERO;

        // 3. Crear estructura base
        ServiceOrder order = ServiceOrder.builder()
                .description(request.getDescription())
                .type(request.getType())
                .status(OrderStatus.PENDIENTE)
                .client(client)
                .laborCost(laborCost)
                .items(new ArrayList<>())
                .build();

        BigDecimal itemsTotalCost = BigDecimal.ZERO;

        // 4. Procesar repuestos
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (OrderItemRequest itemRequest : request.getItems()) {
                Product product = productRepository.findBySku(itemRequest.getProductSku())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemRequest.getProductSku()));

                if (product.getStock() < itemRequest.getQuantity()) {
                    throw new RuntimeException("Stock insuficiente para: " + product.getName());
                }

                product.setStock(product.getStock() - itemRequest.getQuantity());
                productRepository.save(product);

                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                itemsTotalCost = itemsTotalCost.add(itemTotal);

                OrderItem orderItem = OrderItem.builder()
                        .serviceOrder(order)
                        .product(product)
                        .quantity(itemRequest.getQuantity())
                        .price(product.getPrice())
                        .build();

                order.getItems().add(orderItem);
            }
        }

        // 5. Calcular Total Final
        order.setTotalCost(laborCost.add(itemsTotalCost));

        // 6. Guardar
        return orderRepository.save(order);
    }

    // Método para rastreo
    public ServiceOrder getOrderByTrackingCode(String trackingCode) {
        return orderRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con código: " + trackingCode));
    }

    // Método auxiliar por ID
    public ServiceOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }

    // Método para cambiar estado
    public ServiceOrder updateStatus(Long id, OrderStatus newStatus) {
        ServiceOrder order = getOrderById(id);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    // 👇 ESTE ES EL QUE FALTABA PARA EL DASHBOARD 👇
    public List<ServiceOrder> getAllOrders() {
        return orderRepository.findAll();
    }
}