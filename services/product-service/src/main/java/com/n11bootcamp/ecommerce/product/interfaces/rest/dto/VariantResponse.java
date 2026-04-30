package com.n11bootcamp.ecommerce.product.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record VariantResponse(
        UUID id,
        UUID productId,
        String sku,
        String variantValue,
        BigDecimal price,
        int stock,
        int reservedStock,
        int availableStock
) {}
