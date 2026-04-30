package com.n11bootcamp.ecommerce.payment.infrastructure.persistence.adapter;

import com.n11bootcamp.ecommerce.payment.application.port.out.ProcessedEventRepositoryPort;
import com.n11bootcamp.ecommerce.payment.infrastructure.persistence.entity.ProcessedEventEntity;
import com.n11bootcamp.ecommerce.payment.infrastructure.persistence.repository.ProcessedEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ProcessedEventRepositoryAdapter implements ProcessedEventRepositoryPort {

    private final ProcessedEventJpaRepository jpaRepository;

    @Override
    public boolean isAlreadyProcessed(String eventId) {
        return jpaRepository.existsById(eventId);
    }

    @Override
    public void markAsProcessed(String eventId) {
        var entity = new ProcessedEventEntity();
        entity.setEventId(eventId);
        entity.setProcessedAt(Instant.now());
        jpaRepository.save(entity);
    }
}
