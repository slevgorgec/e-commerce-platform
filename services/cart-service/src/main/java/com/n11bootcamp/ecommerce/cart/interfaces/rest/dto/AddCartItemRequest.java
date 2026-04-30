package com.n11bootcamp.ecommerce.cart.interfaces.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddCartItemRequest(
        @NotNull UUID productId,
        @NotNull UUID variantId,
        @Min(1) int quantity
) {}