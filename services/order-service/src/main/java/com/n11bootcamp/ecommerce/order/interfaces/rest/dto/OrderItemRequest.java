package com.n11bootcamp.ecommerce.order.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

/**
 * Sipariş kalemi request DTO'su.
 * Fiyat, ürün adı ve varyant bilgisi Product Service'ten doğrulanarak alınır — client'tan kabul edilmez.
 */
public record OrderItemRequest(
        @NotNull UUID productId,
        @NotNull UUID variantId,
        @Positive int quantity
) {}