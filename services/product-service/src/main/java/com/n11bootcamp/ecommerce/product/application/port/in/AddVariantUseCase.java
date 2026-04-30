package com.n11bootcamp.ecommerce.product.application.port.in;

import com.n11bootcamp.ecommerce.product.application.dto.AddVariantCommand;
import com.n11bootcamp.ecommerce.product.domain.model.ProductVariant;

public interface AddVariantUseCase {
    ProductVariant execute(AddVariantCommand command);
}
