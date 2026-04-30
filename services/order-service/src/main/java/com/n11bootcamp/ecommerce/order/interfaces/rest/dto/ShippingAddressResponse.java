package com.n11bootcamp.ecommerce.order.interfaces.rest.dto;

/**
 * Kargo adresi response DTO'su.
 */
public record ShippingAddressResponse(
        String fullName,
        String phone,
        String addressLine,
        String city,
        String district,
        String postalCode,
        String country
) {}