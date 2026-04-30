package com.n11bootcamp.ecommerce.payment.infrastructure.messaging.publisher;

import com.n11bootcamp.ecommerce.common.event.EventRoutingKeys;
import com.n11bootcamp.ecommerce.common.event.payment.PaymentCompletedEvent;
import com.n11bootcamp.ecommerce.common.event.payment.PaymentFailedEvent;
import com.n11bootcamp.ecommerce.payment.application.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishPaymentCompleted(UUID orderReference, UUID userId, BigDecimal amount, String iyzicoPaymentId) {
        var event = PaymentCompletedEvent.of(orderReference, userId, amount, iyzicoPaymentId);
        rabbitTemplate.convertAndSend(EventRoutingKeys.EXCHANGE, EventRoutingKeys.PAYMENT_COMPLETED, event);
        log.info("PaymentCompletedEvent yayınlandı: orderReference={}, eventId={}", orderReference, event.eventId());
    }

    @Override
    public void publishPaymentFailed(UUID orderReference, UUID userId, String failureReason) {
        var event = PaymentFailedEvent.of(orderReference, userId, failureReason);
        rabbitTemplate.convertAndSend(EventRoutingKeys.EXCHANGE, EventRoutingKeys.PAYMENT_FAILED, event);
        log.info("PaymentFailedEvent yayınlandı: orderReference={}, eventId={}", orderReference, event.eventId());
    }
}
