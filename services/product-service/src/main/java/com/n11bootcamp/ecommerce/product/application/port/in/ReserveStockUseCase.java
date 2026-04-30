package com.n11bootcamp.ecommerce.product.application.port.in;

import com.n11bootcamp.ecommerce.product.application.dto.ReserveStockCommand;

public interface ReserveStockUseCase {
    void execute(ReserveStockCommand command);
}
