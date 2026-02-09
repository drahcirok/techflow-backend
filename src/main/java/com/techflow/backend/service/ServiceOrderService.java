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
        BigDecimal laborCost = request.getLaborCost() != null ? request.getLaborCost() : BigDecimal.ZERO;

        ServiceOrder order = ServiceOrder.builder()
                .description(request.getDescription())
                .type(request.getType())
                .status(OrderStatus.PENDIENTE)
                .laborCost(laborCost)
                .items(new ArrayList<>())
                .build();

        // Si viene clientId, vincular con usuario existente
        if (request.getClientId() != null) {
            User client = userRepository.findById(request.getClientId()).orElse(null);
            if (client != null) {
                order.setClient(client);
                order.setClientEmail(client.getEmail());
                order.setClientName(client.getName());
                order.setClientPhone(client.getPhone());
            }
        }

        // Si viene clientEmail (cliente sin cuenta), guardar sus datos
        if (request.getClientEmail() != null && !request.getClientEmail().isBlank()) {
            order.setClientEmail(request.getClientEmail());
            order.setClientName(request.getClientName());
            order.setClientPhone(request.getClientPhone());

            // Buscar si ya existe un usuario con ese email y vincularlo
            userRepository.findByEmail(request.getClientEmail()).ifPresent(order::setClient);
        }

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

    // 👇 3. MÉTODO PARA OBTENER TODAS LAS ÓRDENES (para admin)
    public List<ServiceOrder> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    // 👇 3.1 MÉTODO PARA OBTENER ÓRDENES DE UN CLIENTE (por ID y email del cliente)
    public List<ServiceOrder> getOrdersByClientId(Long clientId) {
        // Primero obtenemos el email del cliente
        User client = userRepository.findById(clientId).orElse(null);
        if (client != null) {
            // Buscamos por ID o por email para incluir órdenes creadas antes de registrarse
            return orderRepository.findByClientIdOrClientEmailOrderByCreatedAtDesc(clientId, client.getEmail());
        }
        return orderRepository.findByClientIdOrderByCreatedAtDesc(clientId);
    }

    // 👇 3.2 MÉTODO PARA OBTENER ÓRDENES POR EMAIL (vincula órdenes anteriores a cuenta nueva)
    public List<ServiceOrder> getOrdersByClientIdOrEmail(Long clientId, String email) {
        return orderRepository.findByClientIdOrClientEmailOrderByCreatedAtDesc(clientId, email);
    }

    // ⭐ 4. MÉTODO PARA AGREGAR VALORACIÓN
    @Transactional
    public ServiceOrder addRating(Long orderId, Integer rating, String comment) {
        ServiceOrder order = getOrderById(orderId);

        // Solo se puede valorar si está entregado
        if (order.getStatus() != OrderStatus.ENTREGADO) {
            throw new RuntimeException("Solo puedes valorar órdenes entregadas");
        }

        // Solo se puede valorar una vez
        if (order.getRating() != null) {
            throw new RuntimeException("Esta orden ya fue valorada");
        }

        order.setRating(rating);
        order.setRatingComment(comment);
        order.setRatedAt(java.time.LocalDateTime.now());

        return orderRepository.save(order);
    }
}