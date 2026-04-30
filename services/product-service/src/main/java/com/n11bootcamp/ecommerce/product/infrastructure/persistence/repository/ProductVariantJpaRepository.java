package com.n11bootcamp.ecommerce.product.infrastructure.persistence.repository;

import com.n11bootcamp.ecommerce.product.infrastructure.persistence.entity.ProductVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductVariantJpaRepository extends JpaRepository<ProductVariantEntity, UUID> {
    List<ProductVariantEntity> findByProductId(UUID productId);
    boolean existsBySku(String sku);
}
