package com.n11bootcamp.ecommerce.cart.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID userId,
        List<CartItemResponse> items,
        BigDecimal totalAmount,
        int totalItems,
        Instant updatedAt
) {}