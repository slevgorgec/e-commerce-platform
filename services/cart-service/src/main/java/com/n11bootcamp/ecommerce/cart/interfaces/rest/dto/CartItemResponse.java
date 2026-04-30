package com.n11bootcamp.ecommerce.cart.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CartItemResponse(
        UUID productId,
        UUID variantId,
        String productName,
        String variantValue,
        BigDecimal priceSnapshot,
        int quantity,
        BigDecimal lineTotal,
        Instant addedAt
) {}