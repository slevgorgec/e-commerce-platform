package com.n11bootcamp.ecommerce.product.application.port.out;

import com.n11bootcamp.ecommerce.product.domain.model.ProductVariant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductVariantRepositoryPort {
    ProductVariant save(ProductVariant variant);
    Optional<ProductVariant> findById(UUID id);
    List<ProductVariant> findByProductId(UUID productId);
    boolean existsBySku(String sku);
}
