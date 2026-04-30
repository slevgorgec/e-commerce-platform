package com.n11bootcamp.ecommerce.product.application.port.in;

import com.n11bootcamp.ecommerce.product.application.dto.CreateProductCommand;
import com.n11bootcamp.ecommerce.product.domain.model.Product;

public interface CreateProductUseCase {
    Product execute(CreateProductCommand command);
}
