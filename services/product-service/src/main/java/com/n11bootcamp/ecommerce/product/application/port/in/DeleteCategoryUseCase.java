package com.n11bootcamp.ecommerce.product.application.port.in;

import java.util.UUID;

public interface DeleteCategoryUseCase {
    void execute(UUID id);
}
