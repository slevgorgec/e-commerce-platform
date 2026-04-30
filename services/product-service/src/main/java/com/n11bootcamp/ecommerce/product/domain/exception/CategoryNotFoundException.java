package com.n11bootcamp.ecommerce.product.domain.exception;

import java.util.UUID;

public class CategoryNotFoundException extends DomainException {
    public CategoryNotFoundException(UUID id) {
        super("Kategori bulunamadı: " + id);
    }

    public CategoryNotFoundException(String slug) {
        super("Kategori bulunamadı: " + slug);
    }
}
