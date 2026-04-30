package com.n11bootcamp.ecommerce.product.domain.model;

import java.time.Instant;
import java.util.UUID;

public record StockReservation(
        UUID id,
        UUID orderId,
        UUID variantId,
        int quantity,
        ReservationStatus status,
        Instant expiresAt,
        Instant createdAt
) {
    public static StockReservation create(UUID orderId, UUID variantId, int quantity) {
        return new StockReservation(
                UUID.randomUUID(), orderId, variantId, quantity,
                ReservationStatus.PENDING,
                Instant.now().plusSeconds(86400), // 24 saat TTL
                Instant.now()
        );
    }

    public StockReservation commit() {
        return new StockReservation(id, orderId, variantId, quantity, ReservationStatus.COMMITTED,
                expiresAt, createdAt);
    }

    public StockReservation release() {
        return new StockReservation(id, orderId, variantId, quantity, ReservationStatus.RELEASED,
                expiresAt, createdAt);
    }
}
