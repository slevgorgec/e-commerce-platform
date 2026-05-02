package com.n11bootcamp.ecommerce.order.application.dto;

import java.util.List;
import java.util.UUID;

/**
 * Sipariş oluşturma komutu.
 * Controller'dan use case'e taşınan uygulama katmanı DTO'su.
 */
public record CreateOrderCommand(
        UUID userId,
        List<OrderItemData> items,
        ShippingAddressData shippingAddress
) {

    public record OrderItemData(
            UUID productId,
            UUID variantId,
            int quantity
    ) {}

    public record ShippingAddressData(
            String fullName,
            String phone,
            String addressLine,
            String city,
            String district,
            String postalCode,
            String country
    ) {}
}