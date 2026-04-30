package com.n11bootcamp.ecommerce.order.application.usecase;

import com.n11bootcamp.ecommerce.order.application.port.in.HandleStockReservationFailedUseCase;
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
public class HandleStockReservationFailedUseCaseImpl implements HandleStockReservationFailedUseCase {

    private final OrderRepositoryPort orderRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    @Transactional
    public void execute(UUID orderId, String reason) {
        log.info("StockReservationFailed işleniyor: orderId={}, reason={}", orderId, reason);

        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Idempotency: zaten iptal edildiyse tekrar işleme
        if (order.status() == OrderStatus.CANCELLED) {
            log.warn("Sipariş zaten iptal edilmiş: orderId={}", orderId);
            return;
        }

        var updated = order.withStatus(OrderStatus.CANCELLED);
        orderRepository.save(updated);

        log.info("Sipariş CANCELLED yapıldı (stok rezervasyon başarısız): orderId={}, reason={}", orderId, reason);

        eventPublisher.publishOrderCancelled(updated.id(), updated.userId(), reason);
    }
}