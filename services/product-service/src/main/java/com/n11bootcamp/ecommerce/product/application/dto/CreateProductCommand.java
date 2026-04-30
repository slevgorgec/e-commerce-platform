package com.n11bootcamp.ecommerce.product.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductCommand(
        String name,
        String slug,
        String description,
        UUID categoryId,
        BigDecimal basePrice
) {}
