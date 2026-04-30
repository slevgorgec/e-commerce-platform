package com.n11bootcamp.ecommerce.order.infrastructure.messaging.publisher;

import com.n11bootcamp.ecommerce.common.event.EventRoutingKeys;
import com.n11bootcamp.ecommerce.common.event.order.OrderCancelledEvent;
import com.n11bootcamp.ecommerce.common.event.order.OrderConfirmedEvent;
import com.n11bootcamp.ecommerce.common.event.order.OrderCreatedEvent;
import com.n11bootcamp.ecommerce.common.event.payment.PaymentRequestedEvent;
import com.n11bootcamp.ecommerce.order.application.port.out.EventPublisherPort;
import com.n11bootcamp.ecommerce.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishOrderCreated(Order order) {
        var items = order.items().stream()
                .map(item -> new OrderCreatedEvent.OrderItem(
                        item.productId(),
                        item.variantId(),
                        item.quantity(),
                        item.unitPriceSnapshot()
                ))
                .toList();

        var event = OrderCreatedEvent.of(order.id(), order.userId(), items, order.totalAmount());
        rabbitTemplate.convertAndSend(EventRoutingKeys.EXCHANGE, EventRoutingKeys.ORDER_CREATED, event);
        log.info("OrderCreatedEvent yayınlandı: orderId={}, userId={}", order.id(), order.userId());
    }

    @Override
    public void publishPaymentRequested(UUID orderId, UUID userId, BigDecimal amount) {
        var event = PaymentRequestedEvent.of(orderId, userId, amount);
        rabbitTemplate.convertAndSend(EventRoutingKeys.EXCHANGE, EventRoutingKeys.PAYMENT_REQUESTED, event);
        log.info("PaymentRequestedEvent yayınlandı: orderId={}, amount={}", orderId, amount);
    }

    @Override
    public void publishOrderConfirmed(UUID orderId, UUID userId) {
        var event = OrderConfirmedEvent.of(orderId, userId);
        rabbitTemplate.convertAndSend(EventRoutingKeys.EXCHANGE, EventRoutingKeys.ORDER_CONFIRMED, event);
        log.info("OrderConfirmedEvent yayınlandı: orderId={}", orderId);
    }

    @Override
    public void publishOrderCancelled(UUID orderId, UUID userId, String reason) {
        var event = OrderCancelledEvent.of(orderId, userId, reason);
        rabbitTemplate.convertAndSend(EventRoutingKeys.EXCHANGE, EventRoutingKeys.ORDER_CANCELLED, event);
        log.info("OrderCancelledEvent yayınlandı: orderId={}, reason={}", orderId, reason);
    }
}