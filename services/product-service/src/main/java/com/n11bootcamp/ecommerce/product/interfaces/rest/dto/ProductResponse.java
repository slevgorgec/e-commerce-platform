package com.n11bootcamp.ecommerce.product.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String slug,
        String description,
        UUID categoryId,
        BigDecimal basePrice,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
