package com.n11bootcamp.ecommerce.order.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Sipariş response DTO'su.
 */
public record OrderResponse(
        UUID id,
        UUID userId,
        String status,
        BigDecimal totalAmount,
        ShippingAddressResponse shippingAddress,
        List<OrderItemResponse> items,
        Instant createdAt
) {}