package com.n11bootcamp.ecommerce.cart.application.port.out;

import com.n11bootcamp.ecommerce.cart.domain.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepositoryPort {
    Optional<Cart> findByUserId(UUID userId);
    Cart save(Cart cart);
    void deleteByUserId(UUID userId);
    boolean isEventProcessed(String eventId);
    void markEventProcessed(String eventId);
}
