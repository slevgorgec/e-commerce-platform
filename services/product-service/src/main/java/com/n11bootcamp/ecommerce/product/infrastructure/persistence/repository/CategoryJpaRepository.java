package com.n11bootcamp.ecommerce.product.infrastructure.persistence.repository;

import com.n11bootcamp.ecommerce.product.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {
    Optional<CategoryEntity> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
