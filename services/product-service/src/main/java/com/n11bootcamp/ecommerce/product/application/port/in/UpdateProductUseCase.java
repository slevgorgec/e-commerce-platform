package com.n11bootcamp.ecommerce.product.application.port.in;

import com.n11bootcamp.ecommerce.product.application.dto.UpdateProductCommand;
import com.n11bootcamp.ecommerce.product.domain.model.Product;

public interface UpdateProductUseCase {
    Product execute(UpdateProductCommand command);
}
