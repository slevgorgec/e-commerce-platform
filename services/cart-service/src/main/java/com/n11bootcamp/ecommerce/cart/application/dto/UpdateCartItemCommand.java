package com.n11bootcamp.ecommerce.cart.application.dto;

import java.util.UUID;

public record UpdateCartItemCommand(
        UUID userId,
        UUID variantId,
        int quantity
) {}
