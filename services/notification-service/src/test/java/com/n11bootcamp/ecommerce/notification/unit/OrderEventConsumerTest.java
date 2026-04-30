package com.n11bootcamp.ecommerce.notification.unit;

import com.n11bootcamp.ecommerce.common.event.order.OrderCancelledEvent;
import com.n11bootcamp.ecommerce.common.event.order.OrderConfirmedEvent;
import com.n11bootcamp.ecommerce.common.event.order.OrderCreatedEvent;
import com.n11bootcamp.ecommerce.notification.application.port.in.NotificationPort;
import com.n11bootcamp.ecommerce.notification.infrastructure.messaging.consumer.OrderEventConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    @Mock
    private NotificationPort notificationLogger;

    @InjectMocks
    private OrderEventConsumer orderEventConsumer;

    @Test
    void handleOrderCreated_givenValidEvent_delegatesToNotificationLogger() {
        var orderId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var event = new OrderCreatedEvent(
                UUID.randomUUID().toString(), orderId, userId,
                List.of(), new BigDecimal("199.90"), Instant.now()
        );

        orderEventConsumer.handleOrderCreated(event);

        verify(notificationLogger).notifyOrderCreated(orderId, userId, new BigDecimal("199.90"));
    }

    @Test
    void handleOrderConfirmed_givenValidEvent_delegatesToNotificationLogger() {
        var orderId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var event = new OrderConfirmedEvent(UUID.randomUUID().toString(), orderId, userId, Instant.now());

        orderEventConsumer.handleOrderConfirmed(event);

        verify(notificationLogger).notifyOrderConfirmed(orderId, userId);
    }

    @Test
    void handleOrderCancelled_givenValidEvent_delegatesToNotificationLogger() {
        var orderId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var event = new OrderCancelledEvent(
                UUID.randomUUID().toString(), orderId, userId, "Stok yetersiz", Instant.now()
        );

        orderEventConsumer.handleOrderCancelled(event);

        verify(notificationLogger).notifyOrderCancelled(orderId, userId, "Stok yetersiz");
    }
}
