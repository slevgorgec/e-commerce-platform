package com.n11bootcamp.ecommerce.cart.infrastructure.messaging.consumer;

import com.n11bootcamp.ecommerce.cart.application.port.in.ClearCartUseCase;
import com.n11bootcamp.ecommerce.cart.application.port.out.CartRepositoryPort;
import com.n11bootcamp.ecommerce.common.event.order.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ClearCartUseCase clearCartUseCase;
    private final CartRepositoryPort cartRepository;

    @RabbitListener(queues = "cart.order-confirmed")
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        log.info("OrderConfirmedEvent alındı: orderId={}, userId={}", event.orderId(), event.userId());

        if (cartRepository.isEventProcessed(event.eventId())) {
            log.warn("Tekrarlanan event atlandı: eventId={}", event.eventId());
            return;
        }

        clearCartUseCase.execute(event.userId());
        cartRepository.markEventProcessed(event.eventId());

        log.info("Sipariş onaylandı, sepet temizlendi: userId={}", event.userId());
    }
}