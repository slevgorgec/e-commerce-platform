package com.n11bootcamp.ecommerce.cart.interfaces.rest.dto;

import jakarta.validation.constraints.Min;

public record UpdateCartItemRequest(
        @Min(1) int quantity
) {}