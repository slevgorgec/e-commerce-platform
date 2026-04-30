package com.n11bootcamp.ecommerce.cart.application.port.in;

import com.n11bootcamp.ecommerce.cart.domain.model.Cart;

import java.util.UUID;

public interface RemoveCartItemUseCase {
    Cart execute(UUID userId, UUID variantId);
}
