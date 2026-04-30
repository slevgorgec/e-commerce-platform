package com.n11bootcamp.ecommerce.order.infrastructure.persistence.adapter;

import com.n11bootcamp.ecommerce.order.application.port.out.ProcessedEventRepositoryPort;
import com.n11bootcamp.ecommerce.order.infrastructure.persistence.entity.ProcessedEventEntity;
import com.n11bootcamp.ecommerce.order.infrastructure.persistence.repository.ProcessedEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ProcessedEventRepositoryAdapter implements ProcessedEventRepositoryPort {

    private final ProcessedEventJpaRepository jpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean isAlreadyProcessed(String eventId) {
        return jpaRepository.existsById(eventId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsProcessed(String eventId) {
        var entity = new ProcessedEventEntity();
        entity.setEventId(eventId);
        entity.setProcessedAt(Instant.now());
        jpaRepository.save(entity);
    }
}