package com.n11bootcamp.ecommerce.payment.infrastructure.persistence.repository;

import com.n11bootcamp.ecommerce.payment.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {

    Optional<PaymentEntity> findByOrderReference(UUID orderReference);

    Optional<PaymentEntity> findByIyzicoToken(String iyzicoToken);

    boolean existsByOrderReference(UUID orderReference);
}
