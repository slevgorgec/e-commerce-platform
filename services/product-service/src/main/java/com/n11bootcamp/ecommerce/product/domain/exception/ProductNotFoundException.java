package com.n11bootcamp.ecommerce.product.domain.exception;

import java.util.UUID;

public class ProductNotFoundException extends DomainException {
    public ProductNotFoundException(UUID id) {
        super("Ürün bulunamadı: " + id);
    }

    public ProductNotFoundException(String slug) {
        super("Ürün bulunamadı: " + slug);
    }
}
