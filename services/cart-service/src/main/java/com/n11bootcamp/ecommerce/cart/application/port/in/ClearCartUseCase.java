package com.n11bootcamp.ecommerce.cart.application.port.in;

import java.util.UUID;

public interface ClearCartUseCase {
    void execute(UUID userId);
}
