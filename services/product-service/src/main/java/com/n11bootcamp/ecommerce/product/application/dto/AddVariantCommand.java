package com.n11bootcamp.ecommerce.product.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AddVariantCommand(
        UUID productId,
        String sku,
        String variantValue,
        BigDecimal price,
        int stock
) {}
