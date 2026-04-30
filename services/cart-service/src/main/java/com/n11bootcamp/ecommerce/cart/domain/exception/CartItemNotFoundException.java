package com.n11bootcamp.ecommerce.cart.domain.exception;

import java.util.UUID;

public class CartItemNotFoundException extends DomainException {

    public CartItemNotFoundException(UUID variantId) {
        super("Sepette bu varyant bulunamadı: " + variantId);
    }
}