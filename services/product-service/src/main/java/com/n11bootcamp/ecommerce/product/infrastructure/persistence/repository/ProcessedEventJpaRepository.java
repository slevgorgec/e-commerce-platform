package com.n11bootcamp.ecommerce.product.infrastructure.persistence.repository;

import com.n11bootcamp.ecommerce.product.infrastructure.persistence.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEventEntity, String> {}
