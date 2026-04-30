package com.n11bootcamp.ecommerce.order.application.port.in;

import com.n11bootcamp.ecommerce.order.application.dto.PageResult;
import com.n11bootcamp.ecommerce.order.domain.model.Order;

import java.util.UUID;

/**
 * Kullanıcının siparişlerini listeleme use case interface'i (incoming port).
 */
public interface ListOrdersUseCase {

    PageResult<Order> execute(UUID userId, int page, int size);
}