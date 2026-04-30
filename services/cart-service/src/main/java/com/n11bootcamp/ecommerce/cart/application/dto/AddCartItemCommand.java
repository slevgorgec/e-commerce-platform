package com.n11bootcamp.ecommerce.cart.application.dto;

import java.util.UUID;

public record AddCartItemCommand(
        UUID userId,
        UUID productId,
        UUID variantId,
        int quantity
) {}
