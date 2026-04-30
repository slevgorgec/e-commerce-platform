package com.n11bootcamp.ecommerce.product.application.dto;

import java.util.UUID;

public record UpdateCategoryCommand(UUID id, String name, String slug) {}
