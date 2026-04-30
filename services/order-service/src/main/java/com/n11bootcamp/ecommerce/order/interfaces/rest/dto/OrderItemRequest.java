package com.n11bootcamp.ecommerce.order.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Sipariş kalemi request DTO'su.
 */
public record OrderItemRequest(
        @NotNull UUID productId,
        @NotNull UUID variantId,
        @NotBlank String productName,
        @NotBlank String variantValue,
        @NotNull @Positive BigDecimal unitPrice,
        @Positive int quantity
) {}