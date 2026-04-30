package com.n11bootcamp.ecommerce.product.interfaces.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AddVariantRequest(
        @NotBlank @Size(max = 100) String sku,
        @NotBlank @Size(max = 100) String variantValue,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @Min(0) int stock
) {}
