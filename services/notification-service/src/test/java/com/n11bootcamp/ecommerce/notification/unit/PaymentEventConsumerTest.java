package com.n11bootcamp.ecommerce.notification.unit;

import com.n11bootcamp.ecommerce.common.event.payment.PaymentCompletedEvent;
import com.n11bootcamp.ecommerce.common.event.payment.PaymentFailedEvent;
import com.n11bootcamp.ecommerce.notification.application.port.in.NotificationPort;
import com.n11bootcamp.ecommerce.notification.infrastructure.messaging.consumer.PaymentEventConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentEventConsumerTest {

    @Mock
    private NotificationPort notificationLogger;

    @InjectMocks
    private PaymentEventConsumer paymentEventConsumer;

    @Test
    void handlePaymentCompleted_givenValidEvent_delegatesToNotificationLogger() {
        var orderRef = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var event = new PaymentCompletedEvent(
                UUID.randomUUID().toString(), orderRef, userId,
                new BigDecimal("399.90"), "IYZ-001", Instant.now()
        );

        paymentEventConsumer.handlePaymentCompleted(event);

        verify(notificationLogger).notifyPaymentCompleted(orderRef, userId, new BigDecimal("399.90"));
    }

    @Test
    void handlePaymentFailed_givenValidEvent_delegatesToNotificationLogger() {
        var orderRef = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var event = new PaymentFailedEvent(
                UUID.randomUUID().toString(), orderRef, userId, "Yetersiz bakiye", Instant.now()
        );

        paymentEventConsumer.handlePaymentFailed(event);

        verify(notificationLogger).notifyPaymentFailed(orderRef, userId, "Yetersiz bakiye");
    }
}
