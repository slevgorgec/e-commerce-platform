package com.n11bootcamp.ecommerce.product.domain.model;

import java.util.UUID;

public record Category(
        UUID id,
        String name,
        String slug,
        UUID parentId,
        String imageUrl
) {
    public static Category create(String name, String slug, UUID parentId, String imageUrl) {
        return new Category(UUID.randomUUID(), name, slug, parentId, imageUrl);
    }

    public Category withName(String newName, String newSlug, String newImageUrl) {
        return new Category(id, newName, newSlug, parentId, newImageUrl);
    }
}
