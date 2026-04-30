package com.n11bootcamp.ecommerce.payment.infrastructure.persistence.adapter;

import com.n11bootcamp.ecommerce.payment.application.port.out.PaymentRepositoryPort;
import com.n11bootcamp.ecommerce.payment.domain.model.Payment;
import com.n11bootcamp.ecommerce.payment.domain.model.PaymentStatus;
import com.n11bootcamp.ecommerce.payment.infrastructure.persistence.entity.PaymentEntity;
import com.n11bootcamp.ecommerce.payment.infrastructure.persistence.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository jpaRepository;

    @Override
    public Payment save(Payment payment) {
        var entity = toEntity(payment);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Payment> findByOrderReference(UUID orderReference) {
        return jpaRepository.findByOrderReference(orderReference).map(this::toDomain);
    }

    @Override
    public boolean existsByOrderReference(UUID orderReference) {
        return jpaRepository.existsByOrderReference(orderReference);
    }

    private PaymentEntity toEntity(Payment payment) {
        var entity = new PaymentEntity();
        entity.setId(payment.id());
        entity.setOrderReference(payment.orderReference());
        entity.setUserId(payment.userId());
        entity.setAmount(payment.amount());
        entity.setCurrency(payment.currency());
        entity.setStatus(payment.status().name());
        entity.setIyzicoPaymentId(payment.iyzicoPaymentId());
        entity.setIyzicoResponse(payment.iyzicoResponse());
        entity.setCreatedAt(payment.createdAt());
        entity.setUpdatedAt(payment.updatedAt());
        return entity;
    }

    private Payment toDomain(PaymentEntity entity) {
        return new Payment(
                entity.getId(),
                entity.getOrderReference(),
                entity.getUserId(),
                entity.getAmount(),
                entity.getCurrency(),
                PaymentStatus.valueOf(entity.getStatus()),
                entity.getIyzicoPaymentId(),
                entity.getIyzicoResponse(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
