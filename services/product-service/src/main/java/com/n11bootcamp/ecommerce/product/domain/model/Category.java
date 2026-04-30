package com.n11bootcamp.ecommerce.product.domain.model;

import java.util.UUID;

public record Category(
        UUID id,
        String name,
        String slug,
        UUID parentId
) {
    public static Category create(String name, String slug, UUID parentId) {
        return new Category(UUID.randomUUID(), name, slug, parentId);
    }

    public Category withName(String newName, String newSlug) {
        return new Category(id, newName, newSlug, parentId);
    }
}
