package com.n11bootcamp.ecommerce.product.domain.exception;

import java.util.UUID;

public class VariantNotFoundException extends DomainException {
    public VariantNotFoundException(UUID id) {
        super("Varyant bulunamadı: " + id);
    }
}
