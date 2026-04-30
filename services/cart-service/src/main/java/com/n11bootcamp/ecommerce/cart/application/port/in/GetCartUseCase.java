package com.n11bootcamp.ecommerce.cart.application.port.in;

import com.n11bootcamp.ecommerce.cart.domain.model.Cart;

import java.util.UUID;

public interface GetCartUseCase {
    Cart execute(UUID userId);
}
