package com.n11bootcamp.ecommerce.order.infrastructure.persistence.repository;

import com.n11bootcamp.ecommerce.order.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    Page<OrderEntity> findByUserId(UUID userId, Pageable pageable);

    Optional<OrderEntity> findByIdAndUserId(UUID id, UUID userId);
}