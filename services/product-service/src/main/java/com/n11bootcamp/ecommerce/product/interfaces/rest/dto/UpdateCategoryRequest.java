package com.n11bootcamp.ecommerce.product.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String slug,
        @Size(max = 500) String imageUrl
) {}
