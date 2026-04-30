package com.n11bootcamp.ecommerce.order.infrastructure.persistence.repository;

import com.n11bootcamp.ecommerce.order.infrastructure.persistence.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEventEntity, String> {
}