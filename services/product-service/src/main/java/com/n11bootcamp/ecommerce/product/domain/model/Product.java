package com.n11bootcamp.ecommerce.product.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Product(
        UUID id,
        String name,
        String slug,
        String description,
        UUID categoryId,
        BigDecimal basePrice,
        boolean active,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt
) {
    public static Product create(String name, String slug, String description,
                                  UUID categoryId, BigDecimal basePrice, String imageUrl) {
        return new Product(UUID.randomUUID(), name, slug, description, categoryId,
                basePrice, true, imageUrl, Instant.now(), null);
    }

    public Product update(String name, String slug, String description,
                          UUID categoryId, BigDecimal basePrice, boolean active, String imageUrl) {
        return new Product(id, name, slug, description, categoryId, basePrice, active,
                imageUrl, createdAt, Instant.now());
    }

    public Product deactivate() {
        return new Product(id, name, slug, description, categoryId, basePrice, false,
                imageUrl, createdAt, Instant.now());
    }
}
