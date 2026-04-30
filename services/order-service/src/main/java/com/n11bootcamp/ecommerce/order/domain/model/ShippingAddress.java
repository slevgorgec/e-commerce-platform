package com.n11bootcamp.ecommerce.order.domain.model;

/**
 * Kargo adresi değer nesnesi.
 * Sipariş anındaki adres snapshot'ı olarak saklanır.
 */
public record ShippingAddress(
        String fullName,
        String phone,
        String addressLine,
        String city,
        String district,
        String postalCode,
        String country
) {}