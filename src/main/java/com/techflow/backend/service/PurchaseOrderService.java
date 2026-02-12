package com.techflow.backend.service;

import com.techflow.backend.dto.PurchaseOrderRequest;
import com.techflow.backend.entity.Product;
import com.techflow.backend.entity.PurchaseOrder;
import com.techflow.backend.entity.PurchaseOrderItem;
import com.techflow.backend.entity.User;
import com.techflow.backend.enums.PurchaseStatus;
import com.techflow.backend.repository.ProductRepository;
import com.techflow.backend.repository.PurchaseOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.12"); // 12% IVA

    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrderRequest request, User client) {
        // Validar y procesar items
        List<PurchaseOrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (PurchaseOrderRequest.PurchaseItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findBySku(itemRequest.getProductSku())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemRequest.getProductSku()));

            // Validar stock
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + product.getName() +
                        ". Disponible: " + product.getStock());
            }

            // Calcular subtotal del item
            BigDecimal itemSubtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            subtotal = subtotal.add(itemSubtotal);

            // Crear item (se agregará a la orden después)
            PurchaseOrderItem orderItem = PurchaseOrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice())
                    .build();

            orderItems.add(orderItem);
        }

        // Calcular impuestos y total
        BigDecimal tax = subtotal.multiply(TAX_RATE);
        BigDecimal total = subtotal.add(tax);

        // Crear orden de compra
        PurchaseOrder order = PurchaseOrder.builder()
                .client(client)
                .clientName(client.getName())
                .clientEmail(client.getEmail())
                .clientPhone(client.getPhone())
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "EFECTIVO")
                .notes(request.getNotes())
                .status(PurchaseStatus.PENDIENTE)
                .subtotal(subtotal)
                .tax(tax)
                .total(total)
                .items(new ArrayList<>())
                .build();

        // Asociar items a la orden
        for (PurchaseOrderItem item : orderItems) {
            item.setPurchaseOrder(order);
            order.getItems().add(item);
        }

        // Descontar stock
        for (PurchaseOrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        return purchaseOrderRepository.save(order);
    }

    public List<PurchaseOrder> getMyPurchaseOrders(User client) {
        return purchaseOrderRepository.findByClientIdOrderByCreatedAtDesc(client.getId());
    }

    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada"));
    }

    public PurchaseOrder getPurchaseOrderByNumber(String orderNumber) {
        return purchaseOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada"));
    }

    @Transactional
    public PurchaseOrder updateStatus(Long id, PurchaseStatus newStatus) {
        PurchaseOrder order = getPurchaseOrderById(id);
        order.setStatus(newStatus);

        if (newStatus == PurchaseStatus.ENTREGADO && order.getDeliveredAt() == null) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        return purchaseOrderRepository.save(order);
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<PurchaseOrder> getPurchaseOrdersByStatus(PurchaseStatus status) {
        return purchaseOrderRepository.findByStatusOrderByCreatedAtDesc(status);
    }
}
