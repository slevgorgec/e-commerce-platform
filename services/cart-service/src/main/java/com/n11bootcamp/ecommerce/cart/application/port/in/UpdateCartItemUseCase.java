package com.n11bootcamp.ecommerce.cart.application.port.in;

import com.n11bootcamp.ecommerce.cart.application.dto.UpdateCartItemCommand;
import com.n11bootcamp.ecommerce.cart.domain.model.Cart;

public interface UpdateCartItemUseCase {
    Cart execute(UpdateCartItemCommand command);
}
