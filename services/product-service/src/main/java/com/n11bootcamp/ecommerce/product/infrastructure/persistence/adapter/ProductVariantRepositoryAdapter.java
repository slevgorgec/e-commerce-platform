package com.n11bootcamp.ecommerce.product.infrastructure.persistence.adapter;

import com.n11bootcamp.ecommerce.product.application.port.out.ProductVariantRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.model.ProductVariant;
import com.n11bootcamp.ecommerce.product.infrastructure.persistence.entity.ProductVariantEntity;
import com.n11bootcamp.ecommerce.product.infrastructure.persistence.repository.ProductVariantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductVariantRepositoryAdapter implements ProductVariantRepositoryPort {

    private final ProductVariantJpaRepository jpaRepository;

    @Override
    public ProductVariant save(ProductVariant variant) {
        return toModel(jpaRepository.save(toEntity(variant)));
    }

    @Override
    public Optional<ProductVariant> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<ProductVariant> findByProductId(UUID productId) {
        return jpaRepository.findByProductId(productId).stream().map(this::toModel).toList();
    }

    @Override
    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }

    private ProductVariantEntity toEntity(ProductVariant variant) {
        var entity = new ProductVariantEntity();
        entity.setId(variant.id());
        entity.setProductId(variant.productId());
        entity.setSku(variant.sku());
        entity.setVariantValue(variant.variantValue());
        entity.setPrice(variant.price());
        entity.setStock(variant.stock());
        entity.setReservedStock(variant.reservedStock());
        return entity;
    }

    private ProductVariant toModel(ProductVariantEntity entity) {
        return new ProductVariant(
                entity.getId(), entity.getProductId(), entity.getSku(),
                entity.getVariantValue(), entity.getPrice(),
                entity.getStock(), entity.getReservedStock()
        );
    }
}
