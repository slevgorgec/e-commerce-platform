package com.n11bootcamp.ecommerce.order.application.port.in;

import com.n11bootcamp.ecommerce.order.domain.model.Order;

import java.util.UUID;

/**
 * Sipariş sorgulama use case interface'i (incoming port).
 */
public interface GetOrderUseCase {

    Order getById(UUID id);

    /**
     * Kullanıcı erişim kontrolü ile sipariş getirir.
     * Sipariş belirtilen kullanıcıya ait değilse OrderNotFoundException fırlatır.
     */
    Order getByIdAndUserId(UUID id, UUID userId);
}