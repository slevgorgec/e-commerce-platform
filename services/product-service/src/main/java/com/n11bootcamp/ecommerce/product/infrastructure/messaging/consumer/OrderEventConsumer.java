package com.n11bootcamp.ecommerce.product.infrastructure.messaging.consumer;

import com.n11bootcamp.ecommerce.common.event.order.OrderCancelledEvent;
import com.n11bootcamp.ecommerce.common.event.order.OrderCreatedEvent;
import com.n11bootcamp.ecommerce.product.application.dto.ReserveStockCommand;
import com.n11bootcamp.ecommerce.product.application.port.in.ReleaseStockUseCase;
import com.n11bootcamp.ecommerce.product.application.port.in.ReserveStockUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.ProcessedEventRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ReserveStockUseCase reserveStockUseCase;
    private final ReleaseStockUseCase releaseStockUseCase;
    private final ProcessedEventRepositoryPort processedEventRepository;

    @RabbitListener(queues = "product.order-created")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("OrderCreatedEvent alındı: orderId={}", event.orderId());

        if (processedEventRepository.isAlreadyProcessed(event.eventId())) {
            log.warn("Tekrarlanan event atlandı: eventId={}", event.eventId());
            return;
        }

        var items = event.items().stream()
                .map(i -> new ReserveStockCommand.Item(i.variantId(), i.quantity()))
                .toList();
        reserveStockUseCase.execute(new ReserveStockCommand(event.orderId(), items));

        processedEventRepository.markAsProcessed(event.eventId());
    }

    @RabbitListener(queues = "product.order-cancelled")
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("OrderCancelledEvent alındı: orderId={}", event.orderId());

        if (processedEventRepository.isAlreadyProcessed(event.eventId())) {
            log.warn("Tekrarlanan event atlandı: eventId={}", event.eventId());
            return;
        }

        releaseStockUseCase.execute(event.orderId());
        processedEventRepository.markAsProcessed(event.eventId());
    }
}
