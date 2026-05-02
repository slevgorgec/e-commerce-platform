package com.n11bootcamp.ecommerce.product.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCategoryRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String slug,
        UUID parentId,
        @Size(max = 500) String imageUrl
) {}
