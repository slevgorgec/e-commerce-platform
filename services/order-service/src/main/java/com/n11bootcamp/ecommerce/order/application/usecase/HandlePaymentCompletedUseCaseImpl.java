package com.n11bootcamp.ecommerce.order.application.usecase;

import com.n11bootcamp.ecommerce.order.application.port.in.HandlePaymentCompletedUseCase;
import com.n11bootcamp.ecommerce.order.application.port.out.EventPublisherPort;
import com.n11bootcamp.ecommerce.order.application.port.out.OrderRepositoryPort;
import com.n11bootcamp.ecommerce.order.domain.exception.OrderNotFoundException;
import com.n11bootcamp.ecommerce.order.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandlePaymentCompletedUseCaseImpl implements HandlePaymentCompletedUseCase {

    private final OrderRepositoryPort orderRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    @Transactional
    public void execute(UUID orderId) {
        log.info("PaymentCompleted işleniyor: orderId={}", orderId);

        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Idempotency: zaten onaylandıysa tekrar işleme
        if (order.status() == OrderStatus.CONFIRMED) {
            log.warn("Sipariş zaten onaylanmış: orderId={}", orderId);
            return;
        }

        var updated = order.withStatus(OrderStatus.CONFIRMED);
        orderRepository.save(updated);

        log.info("Sipariş CONFIRMED yapıldı: orderId={}", orderId);

        eventPublisher.publishOrderConfirmed(updated.id(), updated.userId());
    }
}