package com.n11bootcamp.ecommerce.order.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Kargo adresi request DTO'su.
 */
public record ShippingAddressRequest(
        @NotBlank String fullName,
        @NotBlank String phone,
        @NotBlank String addressLine,
        @NotBlank String city,
        @NotBlank String district,
        String postalCode,
        @NotBlank String country
) {}