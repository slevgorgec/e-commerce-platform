package com.n11bootcamp.ecommerce.product.application.dto;

import java.util.UUID;

public record CreateCategoryCommand(String name, String slug, UUID parentId, String imageUrl) {}
