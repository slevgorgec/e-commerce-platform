package com.n11bootcamp.ecommerce.order.infrastructure.messaging.consumer;

import com.n11bootcamp.ecommerce.common.event.product.StockReservationFailedEvent;
import com.n11bootcamp.ecommerce.common.event.product.StockReservedEvent;
import com.n11bootcamp.ecommerce.order.application.port.in.HandleStockReservationFailedUseCase;
import com.n11bootcamp.ecommerce.order.application.port.in.HandleStockReservedUseCase;
import com.n11bootcamp.ecommerce.order.application.port.out.ProcessedEventRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventConsumer {

    private final HandleStockReservedUseCase handleStockReservedUseCase;
    private final HandleStockReservationFailedUseCase handleStockReservationFailedUseCase;
    private final ProcessedEventRepositoryPort processedEventRepository;

    @RabbitListener(queues = "order.stock-reserved")
    public void handleStockReserved(StockReservedEvent event) {
        log.info("StockReservedEvent alındı: orderId={}, eventId={}", event.orderId(), event.eventId());

        if (processedEventRepository.isAlreadyProcessed(event.eventId())) {
            log.warn("Tekrarlanan event atlandı: eventId={}", event.eventId());
            return;
        }

        handleStockReservedUseCase.execute(event.orderId());
        processedEventRepository.markAsProcessed(event.eventId());
    }

    @RabbitListener(queues = "order.stock-reservation-failed")
    public void handleStockReservationFailed(StockReservationFailedEvent event) {
        log.info("StockReservationFailedEvent alındı: orderId={}, reason={}, eventId={}",
                event.orderId(), event.reason(), event.eventId());

        if (processedEventRepository.isAlreadyProcessed(event.eventId())) {
            log.warn("Tekrarlanan event atlandı: eventId={}", event.eventId());
            return;
        }

        handleStockReservationFailedUseCase.execute(event.orderId(), event.reason());
        processedEventRepository.markAsProcessed(event.eventId());
    }
}