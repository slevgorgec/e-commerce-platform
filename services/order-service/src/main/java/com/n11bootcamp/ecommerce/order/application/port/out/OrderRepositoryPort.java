package com.n11bootcamp.ecommerce.order.application.port.out;

import com.n11bootcamp.ecommerce.order.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Sipariş kalıcılık port'u (outgoing port).
 * Infrastructure katmanı tarafından implement edilir.
 */
public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    Optional<Order> findByIdAndUserId(UUID id, UUID userId);

    Page<Order> findByUserId(UUID userId, Pageable pageable);
}