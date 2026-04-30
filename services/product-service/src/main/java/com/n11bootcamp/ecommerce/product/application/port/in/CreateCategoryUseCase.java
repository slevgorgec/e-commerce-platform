package com.n11bootcamp.ecommerce.product.application.port.in;

import com.n11bootcamp.ecommerce.product.application.dto.CreateCategoryCommand;
import com.n11bootcamp.ecommerce.product.domain.model.Category;

public interface CreateCategoryUseCase {
    Category execute(CreateCategoryCommand command);
}
