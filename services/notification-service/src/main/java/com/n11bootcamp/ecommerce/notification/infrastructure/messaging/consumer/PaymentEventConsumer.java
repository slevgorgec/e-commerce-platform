package com.n11bootcamp.ecommerce.notification.infrastructure.messaging.consumer;

import com.n11bootcamp.ecommerce.common.event.payment.PaymentCompletedEvent;
import com.n11bootcamp.ecommerce.common.event.payment.PaymentFailedEvent;
import com.n11bootcamp.ecommerce.notification.application.port.in.NotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final NotificationPort notificationLogger;

    @RabbitListener(queues = "notification.payment-completed")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("PaymentCompletedEvent alındı: orderReference={}, eventId={}",
                event.orderReference(), event.eventId());
        notificationLogger.notifyPaymentCompleted(event.orderReference(), event.userId(), event.amount());
    }

    @RabbitListener(queues = "notification.payment-failed")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("PaymentFailedEvent alındı: orderReference={}, reason={}, eventId={}",
                event.orderReference(), event.failureReason(), event.eventId());
        notificationLogger.notifyPaymentFailed(event.orderReference(), event.userId(), event.failureReason());
    }
}
