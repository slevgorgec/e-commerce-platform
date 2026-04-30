package com.n11bootcamp.ecommerce.order.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Sipariş kalemi response DTO'su.
 */
public record OrderItemResponse(
        UUID id,
        UUID productId,
        UUID variantId,
        String productNameSnapshot,
        String variantValueSnapshot,
        BigDecimal unitPriceSnapshot,
        int quantity
) {}