package com.n11bootcamp.ecommerce.cart.application.port.in;

import com.n11bootcamp.ecommerce.cart.application.dto.AddCartItemCommand;
import com.n11bootcamp.ecommerce.cart.domain.model.Cart;

public interface AddCartItemUseCase {
    Cart execute(AddCartItemCommand command);
}
