package com.n11bootcamp.ecommerce.payment.infrastructure.messaging.consumer;

import com.n11bootcamp.ecommerce.common.event.payment.PaymentRequestedEvent;
import com.n11bootcamp.ecommerce.payment.application.dto.InitiatePaymentCommand;
import com.n11bootcamp.ecommerce.payment.application.port.in.InitiatePaymentUseCase;
import com.n11bootcamp.ecommerce.payment.application.port.out.ProcessedEventRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestedConsumer {

    private final InitiatePaymentUseCase initiatePaymentUseCase;
    private final ProcessedEventRepositoryPort processedEventRepository;

    @RabbitListener(queues = "payment.payment-requested")
    public void handlePaymentRequested(PaymentRequestedEvent event) {
        log.info("PaymentRequestedEvent alındı: orderReference={}, amount={}, eventId={}",
                event.orderReference(), event.amount(), event.eventId());

        if (processedEventRepository.isAlreadyProcessed(event.eventId())) {
            log.warn("Tekrarlanan event atlandı: eventId={}", event.eventId());
            return;
        }

        var command = new InitiatePaymentCommand(
                event.orderReference(),
                event.userId(),
                event.amount(),
                event.currency()
        );

        initiatePaymentUseCase.execute(command);
        processedEventRepository.markAsProcessed(event.eventId());
    }
}