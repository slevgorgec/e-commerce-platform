package com.n11bootcamp.ecommerce.cart.infrastructure.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record VariantInfoResponse(
        UUID productId,
        UUID variantId,
        String productName,
        String variantValue,
        BigDecimal price,
        boolean active
) {}
