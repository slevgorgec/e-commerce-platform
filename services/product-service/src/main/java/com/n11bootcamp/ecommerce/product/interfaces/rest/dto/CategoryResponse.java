package com.n11bootcamp.ecommerce.product.interfaces.rest.dto;

import java.util.UUID;

public record CategoryResponse(UUID id, String name, String slug, UUID parentId) {}
