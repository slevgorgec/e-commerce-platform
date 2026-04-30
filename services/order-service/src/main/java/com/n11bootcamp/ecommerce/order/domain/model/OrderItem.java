package com.n11bootcamp.ecommerce.order.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Sipariş kalemi domain modeli.
 * Ürün bilgileri sipariş anındaki snapshot olarak saklanır.
 */
public record OrderItem(
        UUID id,
        UUID orderId,
        UUID productId,
        UUID variantId,
        String productNameSnapshot,
        String variantValueSnapshot,
        BigDecimal unitPriceSnapshot,
        int quantity
) {}