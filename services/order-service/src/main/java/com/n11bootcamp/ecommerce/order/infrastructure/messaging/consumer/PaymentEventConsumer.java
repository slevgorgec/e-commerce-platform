package com.n11bootcamp.ecommerce.order.infrastructure.messaging.consumer;

import com.n11bootcamp.ecommerce.common.event.payment.PaymentCompletedEvent;
import com.n11bootcamp.ecommerce.common.event.payment.PaymentFailedEvent;
import com.n11bootcamp.ecommerce.order.application.port.in.HandlePaymentCompletedUseCase;
import com.n11bootcamp.ecommerce.order.application.port.in.HandlePaymentFailedUseCase;
import com.n11bootcamp.ecommerce.order.application.port.out.ProcessedEventRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final HandlePaymentCompletedUseCase handlePaymentCompletedUseCase;
    private final HandlePaymentFailedUseCase handlePaymentFailedUseCase;
    private final ProcessedEventRepositoryPort processedEventRepository;

    @RabbitListener(queues = "order.payment-completed")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("PaymentCompletedEvent alındı: orderReference={}, eventId={}", event.orderReference(), event.eventId());

        if (processedEventRepository.isAlreadyProcessed(event.eventId())) {
            log.warn("Tekrarlanan event atlandı: eventId={}", event.eventId());
            return;
        }

        // orderReference = orderId (Order Service context'inde)
        handlePaymentCompletedUseCase.execute(event.orderReference());
        processedEventRepository.markAsProcessed(event.eventId());
    }

    @RabbitListener(queues = "order.payment-failed")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("PaymentFailedEvent alındı: orderReference={}, reason={}, eventId={}",
                event.orderReference(), event.failureReason(), event.eventId());

        if (processedEventRepository.isAlreadyProcessed(event.eventId())) {
            log.warn("Tekrarlanan event atlandı: eventId={}", event.eventId());
            return;
        }

        handlePaymentFailedUseCase.execute(event.orderReference());
        processedEventRepository.markAsProcessed(event.eventId());
    }
}