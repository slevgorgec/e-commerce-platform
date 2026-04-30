package com.n11bootcamp.ecommerce.product.infrastructure.messaging.publisher;

import com.n11bootcamp.ecommerce.common.event.EventRoutingKeys;
import com.n11bootcamp.ecommerce.common.event.product.StockCommittedEvent;
import com.n11bootcamp.ecommerce.common.event.product.StockReleasedEvent;
import com.n11bootcamp.ecommerce.common.event.product.StockReservationFailedEvent;
import com.n11bootcamp.ecommerce.common.event.product.StockReservedEvent;
import com.n11bootcamp.ecommerce.product.application.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishStockReserved(UUID orderId) {
        var event = StockReservedEvent.of(orderId);
        rabbitTemplate.convertAndSend(EventRoutingKeys.EXCHANGE, EventRoutingKeys.STOCK_RESERVED, event);
        log.info("StockReservedEvent yayınlandı: orderId={}", orderId);
    }

    @Override
    public void publishStockReservationFailed(UUID orderId, String reason) {
        var event = StockReservationFailedEvent.of(orderId, reason);
        rabbitTemplate.convertAndSend(EventRoutingKeys.EXCHANGE, EventRoutingKeys.STOCK_RESERVATION_FAILED, event);
        log.info("StockReservationFailedEvent yayınlandı: orderId={}", orderId);
    }

    @Override
    public void publishStockCommitted(UUID orderId) {
        var event = StockCommittedEvent.of(orderId);
        rabbitTemplate.convertAndSend(EventRoutingKeys.EXCHANGE, EventRoutingKeys.STOCK_COMMITTED, event);
        log.info("StockCommittedEvent yayınlandı: orderId={}", orderId);
    }

    @Override
    public void publishStockReleased(UUID orderId) {
        var event = StockReleasedEvent.of(orderId);
        rabbitTemplate.convertAndSend(EventRoutingKeys.EXCHANGE, EventRoutingKeys.STOCK_RELEASED, event);
        log.info("StockReleasedEvent yayınlandı: orderId={}", orderId);
    }
}
