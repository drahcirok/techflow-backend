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
        // ... (Tu código de crear orden se mantiene igual, no lo borres) ...
        // Resumido para no llenar la pantalla, pero aquí va toda la lógica de crear
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        BigDecimal laborCost = request.getLaborCost() != null ? request.getLaborCost() : BigDecimal.ZERO;

        ServiceOrder order = ServiceOrder.builder()
                .description(request.getDescription())
                .type(request.getType())
                .status(OrderStatus.PENDIENTE)
                .client(client)
                .laborCost(laborCost)
                .items(new ArrayList<>())
                .build();

        BigDecimal itemsTotalCost = BigDecimal.ZERO;
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
                        .serviceOrder(order).product(product).quantity(itemRequest.getQuantity()).price(product.getPrice()).build();
                order.getItems().add(orderItem);
            }
        }
        order.setTotalCost(laborCost.add(itemsTotalCost));
        return orderRepository.save(order);
    }

    public ServiceOrder getOrderByTrackingCode(String trackingCode) {
        return orderRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }

    public ServiceOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }

    public ServiceOrder updateStatus(Long id, OrderStatus newStatus) {
        ServiceOrder order = getOrderById(id);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    // 👇 1. MÉTODO PARA EL DASHBOARD (Técnico y Admin)
    public List<ServiceOrder> getAllActiveOrders() {
        // Ocultamos lo que ya se entregó o se canceló
        List<OrderStatus> hiddenStatuses = List.of(OrderStatus.ENTREGADO, OrderStatus.CANCELADO);
        return orderRepository.findByStatusNotInOrderByCreatedAtDesc(hiddenStatuses);
    }

    // 👇 2. MÉTODO PARA EL HISTORIAL
    public List<ServiceOrder> getHistoryOrders() {
        // Mostramos solo lo finalizado
        List<OrderStatus> historyStatuses = List.of(OrderStatus.ENTREGADO, OrderStatus.CANCELADO);
        return orderRepository.findByStatusInOrderByCreatedAtDesc(historyStatuses);
    }
}