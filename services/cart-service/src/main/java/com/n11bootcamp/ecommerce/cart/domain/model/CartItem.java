package com.n11bootcamp.ecommerce.cart.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CartItem(
        UUID productId,
        UUID variantId,
        String productName,
        String variantValue,
        BigDecimal priceSnapshot,
        int quantity,
        Instant addedAt
) {
    public static CartItem of(UUID productId, UUID variantId, String productName,
                               String variantValue, BigDecimal priceSnapshot, int quantity) {
        return new CartItem(productId, variantId, productName, variantValue,
                priceSnapshot, quantity, Instant.now());
    }

    public CartItem withQuantity(int newQuantity) {
        return new CartItem(productId, variantId, productName, variantValue,
                priceSnapshot, newQuantity, addedAt);
    }
}
