package com.n11bootcamp.ecommerce.product.infrastructure.persistence.adapter;

import com.n11bootcamp.ecommerce.product.application.port.out.StockReservationRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.model.ReservationStatus;
import com.n11bootcamp.ecommerce.product.domain.model.StockReservation;
import com.n11bootcamp.ecommerce.product.infrastructure.persistence.entity.StockReservationEntity;
import com.n11bootcamp.ecommerce.product.infrastructure.persistence.repository.StockReservationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StockReservationRepositoryAdapter implements StockReservationRepositoryPort {

    private final StockReservationJpaRepository jpaRepository;

    @Override
    public StockReservation save(StockReservation reservation) {
        return toModel(jpaRepository.save(toEntity(reservation)));
    }

    @Override
    public List<StockReservation> findByOrderId(UUID orderId) {
        return jpaRepository.findByOrderId(orderId).stream().map(this::toModel).toList();
    }

    @Override
    public List<StockReservation> findByOrderIdAndStatus(UUID orderId, ReservationStatus status) {
        return jpaRepository.findByOrderIdAndStatus(orderId, status.name())
                .stream().map(this::toModel).toList();
    }

    @Override
    public void saveAll(List<StockReservation> reservations) {
        jpaRepository.saveAll(reservations.stream().map(this::toEntity).toList());
    }

    private StockReservationEntity toEntity(StockReservation reservation) {
        var entity = new StockReservationEntity();
        entity.setId(reservation.id());
        entity.setOrderId(reservation.orderId());
        entity.setVariantId(reservation.variantId());
        entity.setQuantity(reservation.quantity());
        entity.setStatus(reservation.status().name());
        entity.setExpiresAt(reservation.expiresAt());
        entity.setCreatedAt(reservation.createdAt());
        return entity;
    }

    private StockReservation toModel(StockReservationEntity entity) {
        return new StockReservation(
                entity.getId(), entity.getOrderId(), entity.getVariantId(),
                entity.getQuantity(), ReservationStatus.valueOf(entity.getStatus()),
                entity.getExpiresAt(), entity.getCreatedAt()
        );
    }
}
