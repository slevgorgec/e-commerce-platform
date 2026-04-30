package com.n11bootcamp.ecommerce.product.infrastructure.persistence.repository;

import com.n11bootcamp.ecommerce.product.infrastructure.persistence.entity.StockReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockReservationJpaRepository extends JpaRepository<StockReservationEntity, UUID> {
    List<StockReservationEntity> findByOrderId(UUID orderId);
    List<StockReservationEntity> findByOrderIdAndStatus(UUID orderId, String status);
}
