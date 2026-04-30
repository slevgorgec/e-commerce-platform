package com.n11bootcamp.ecommerce.order.application.usecase;

import com.n11bootcamp.ecommerce.order.application.port.in.GetOrderUseCase;
import com.n11bootcamp.ecommerce.order.application.port.out.OrderRepositoryPort;
import com.n11bootcamp.ecommerce.order.domain.exception.OrderNotFoundException;
import com.n11bootcamp.ecommerce.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetOrderUseCaseImpl implements GetOrderUseCase {

    private final OrderRepositoryPort orderRepository;

    @Override
    @Transactional(readOnly = true)
    public Order getById(UUID id) {
        log.debug("Sipariş getiriliyor: orderId={}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Order getByIdAndUserId(UUID id, UUID userId) {
        log.debug("Sipariş getiriliyor: orderId={}, userId={}", id, userId);
        return orderRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}