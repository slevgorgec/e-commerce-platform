package com.n11bootcamp.ecommerce.product.interfaces.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 255) String slug,
        String description,
        UUID categoryId,
        @NotNull @DecimalMin("0.01") BigDecimal basePrice,
        @Size(max = 500) String imageUrl
) {}
