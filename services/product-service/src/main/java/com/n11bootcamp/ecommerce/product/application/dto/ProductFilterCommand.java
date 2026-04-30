package com.n11bootcamp.ecommerce.product.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductFilterCommand(
        UUID categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String search,
        int page,
        int size,
        String sortBy,
        String sortDir
) {
    public ProductFilterCommand {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;
        if (sortBy == null || sortBy.isBlank()) sortBy = "createdAt";
        if (sortDir == null || sortDir.isBlank()) sortDir = "desc";
    }
}
