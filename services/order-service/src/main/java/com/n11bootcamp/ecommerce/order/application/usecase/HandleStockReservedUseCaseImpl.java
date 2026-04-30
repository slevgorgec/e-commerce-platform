package com.n11bootcamp.ecommerce.order.application.usecase;

import com.n11bootcamp.ecommerce.order.application.port.in.HandleStockReservedUseCase;
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
public class HandleStockReservedUseCaseImpl implements HandleStockReservedUseCase {

    private final OrderRepositoryPort orderRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    @Transactional
    public void execute(UUID orderId) {
        log.info("StockReserved işleniyor: orderId={}", orderId);

        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Idempotency: zaten STOCK_RESERVED ise payment event'ini yeniden yayınla
        if (order.status() == OrderStatus.STOCK_RESERVED || order.status() == OrderStatus.PAYMENT_REQUESTED) {
            log.warn("Sipariş zaten stok rezervasyonlu durumda, PaymentRequested yeniden yayınlanıyor: orderId={}", orderId);
            eventPublisher.publishPaymentRequested(order.id(), order.userId(), order.totalAmount());
            return;
        }

        var updated = order.withStatus(OrderStatus.STOCK_RESERVED);
        orderRepository.save(updated);

        log.info("Sipariş STOCK_RESERVED yapıldı: orderId={}", orderId);

        eventPublisher.publishPaymentRequested(updated.id(), updated.userId(), updated.totalAmount());
    }
}