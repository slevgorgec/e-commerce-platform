package com.n11bootcamp.ecommerce.notification.infrastructure.messaging.consumer;

import com.n11bootcamp.ecommerce.common.event.order.OrderCancelledEvent;
import com.n11bootcamp.ecommerce.common.event.order.OrderConfirmedEvent;
import com.n11bootcamp.ecommerce.common.event.order.OrderCreatedEvent;
import com.n11bootcamp.ecommerce.notification.application.port.in.NotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final NotificationPort notificationLogger;

    @RabbitListener(queues = "notification.order-created")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("OrderCreatedEvent alındı: orderId={}, eventId={}", event.orderId(), event.eventId());
        notificationLogger.notifyOrderCreated(event.orderId(), event.userId(), event.totalAmount());
    }

    @RabbitListener(queues = "notification.order-confirmed")
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        log.info("OrderConfirmedEvent alındı: orderId={}, eventId={}", event.orderId(), event.eventId());
        notificationLogger.notifyOrderConfirmed(event.orderId(), event.userId());
    }

    @RabbitListener(queues = "notification.order-cancelled")
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("OrderCancelledEvent alındı: orderId={}, reason={}, eventId={}",
                event.orderId(), event.reason(), event.eventId());
        notificationLogger.notifyOrderCancelled(event.orderId(), event.userId(), event.reason());
    }
}
