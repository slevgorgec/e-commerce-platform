package com.n11bootcamp.ecommerce.product.infrastructure.messaging.consumer;

import com.n11bootcamp.ecommerce.common.event.payment.PaymentCompletedEvent;
import com.n11bootcamp.ecommerce.common.event.payment.PaymentFailedEvent;
import com.n11bootcamp.ecommerce.product.application.port.in.CommitStockUseCase;
import com.n11bootcamp.ecommerce.product.application.port.in.ReleaseStockUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.ProcessedEventRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final CommitStockUseCase commitStockUseCase;
    private final ReleaseStockUseCase releaseStockUseCase;
    private final ProcessedEventRepositoryPort processedEventRepository;

    @RabbitListener(queues = "product.payment-completed")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("PaymentCompletedEvent alındı: orderReference={}", event.orderReference());

        if (processedEventRepository.isAlreadyProcessed(event.eventId())) {
            log.warn("Tekrarlanan event atlandı: eventId={}", event.eventId());
            return;
        }

        // orderReference = orderId (Product Service perspektifinden stok rezervasyonunu bul)
        commitStockUseCase.execute(event.orderReference());
        processedEventRepository.markAsProcessed(event.eventId());
    }

    @RabbitListener(queues = "product.payment-failed")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("PaymentFailedEvent alındı: orderReference={}", event.orderReference());

        if (processedEventRepository.isAlreadyProcessed(event.eventId())) {
            log.warn("Tekrarlanan event atlandı: eventId={}", event.eventId());
            return;
        }

        releaseStockUseCase.execute(event.orderReference());
        processedEventRepository.markAsProcessed(event.eventId());
    }
}
