package com.n11bootcamp.ecommerce.order.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record VariantInfo(
        UUID productId,
        UUID variantId,
        String productName,
        String variantValue,
        BigDecimal price,
        boolean active
) {}
