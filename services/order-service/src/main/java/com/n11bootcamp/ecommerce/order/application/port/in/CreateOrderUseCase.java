package com.n11bootcamp.ecommerce.order.application.port.in;

import com.n11bootcamp.ecommerce.order.application.dto.CreateOrderCommand;
import com.n11bootcamp.ecommerce.order.domain.model.Order;

/**
 * Yeni sipariş oluşturma use case interface'i (incoming port).
 */
public interface CreateOrderUseCase {

    Order execute(CreateOrderCommand command);
}