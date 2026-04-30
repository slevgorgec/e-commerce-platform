package com.n11bootcamp.ecommerce.order.application.port.out;

import com.n11bootcamp.ecommerce.order.domain.model.Order;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event yayımlama port'u (outgoing port).
 * RabbitMQ infrastructure katmanı tarafından implement edilir.
 */
public interface EventPublisherPort {

    void publishOrderCreated(Order order);

    void publishPaymentRequested(UUID orderId, UUID userId, BigDecimal amount);

    void publishOrderConfirmed(UUID orderId, UUID userId);

    void publishOrderCancelled(UUID orderId, UUID userId, String reason);
}