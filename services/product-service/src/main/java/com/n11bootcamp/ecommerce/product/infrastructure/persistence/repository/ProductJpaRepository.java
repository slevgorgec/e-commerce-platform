package com.n11bootcamp.ecommerce.product.infrastructure.persistence.repository;

import com.n11bootcamp.ecommerce.product.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findBySlug(String slug);
    boolean existsBySlug(String slug);

    @Query("""
            SELECT p FROM ProductEntity p
            WHERE p.active = true
            AND (:categoryId IS NULL OR p.categoryId = :categoryId)
            AND (:minPrice IS NULL OR p.basePrice >= :minPrice)
            AND (:maxPrice IS NULL OR p.basePrice <= :maxPrice)
            AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:search AS String), '%')))
            """)
    Page<ProductEntity> findFiltered(
            @Param("categoryId") UUID categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("search") String search,
            Pageable pageable
    );
}
