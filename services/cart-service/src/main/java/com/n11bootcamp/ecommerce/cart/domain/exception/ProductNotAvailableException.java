package com.n11bootcamp.ecommerce.cart.domain.exception;

import java.util.UUID;

public class ProductNotAvailableException extends DomainException {

    public ProductNotAvailableException(UUID variantId) {
        super("Ürün sepete eklenemez, aktif değil: " + variantId);
    }
}