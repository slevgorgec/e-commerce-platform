package com.n11bootcamp.ecommerce.product.application.port.in;

import com.n11bootcamp.ecommerce.product.domain.model.Category;

import java.util.List;
import java.util.UUID;

public interface GetCategoryUseCase {
    Category getById(UUID id);
    List<Category> getAll();
}
