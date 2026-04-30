package com.n11bootcamp.ecommerce.order.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Sipariş domain modeli (aggregate root).
 * Saga Choreography'nin başlatıcısı.
 */
public record Order(
        UUID id,
        UUID userId,
        OrderStatus status,
        BigDecimal totalAmount,
        ShippingAddress shippingAddress,
        List<OrderItem> items,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Yeni sipariş oluşturur.
     * totalAmount, items listesinden otomatik hesaplanır.
     */
    public static Order create(UUID userId, List<OrderItem> items, ShippingAddress shippingAddress) {
        var totalAmount = items.stream()
                .map(item -> item.unitPriceSnapshot().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Order(
                UUID.randomUUID(),
                userId,
                OrderStatus.PENDING,
                totalAmount,
                shippingAddress,
                items,
                Instant.now(),
                null
        );
    }

    /**
     * Durumu güncellenmiş yeni Order instance döner (immutable pattern).
     */
    public Order withStatus(OrderStatus newStatus) {
        return new Order(id, userId, newStatus, totalAmount, shippingAddress, items, createdAt, Instant.now());
    }
}