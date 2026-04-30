package com.n11bootcamp.ecommerce.order.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Sipariş oluşturma request DTO'su.
 */
public record CreateOrderRequest(
        @NotEmpty @Valid List<OrderItemRequest> items,
        @NotNull @Valid ShippingAddressRequest shippingAddress
) {}