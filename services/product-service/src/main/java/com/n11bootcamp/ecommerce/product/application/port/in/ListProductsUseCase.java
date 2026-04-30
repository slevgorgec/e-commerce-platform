package com.n11bootcamp.ecommerce.product.application.port.in;

import com.n11bootcamp.ecommerce.product.application.dto.PageResult;
import com.n11bootcamp.ecommerce.product.application.dto.ProductFilterCommand;
import com.n11bootcamp.ecommerce.product.domain.model.Product;

public interface ListProductsUseCase {
    PageResult<Product> execute(ProductFilterCommand filter);
}
