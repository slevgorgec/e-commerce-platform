package com.n11bootcamp.ecommerce.product.application.port.in;

import com.n11bootcamp.ecommerce.product.application.dto.UpdateCategoryCommand;
import com.n11bootcamp.ecommerce.product.domain.model.Category;

public interface UpdateCategoryUseCase {
    Category execute(UpdateCategoryCommand command);
}
