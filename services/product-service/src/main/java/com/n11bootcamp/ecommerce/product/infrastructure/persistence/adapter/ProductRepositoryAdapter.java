package com.n11bootcamp.ecommerce.product.infrastructure.persistence.adapter;

import com.n11bootcamp.ecommerce.product.application.dto.PageResult;
import com.n11bootcamp.ecommerce.product.application.dto.ProductFilterCommand;
import com.n11bootcamp.ecommerce.product.application.port.out.ProductRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.model.Product;
import com.n11bootcamp.ecommerce.product.infrastructure.persistence.entity.ProductEntity;
import com.n11bootcamp.ecommerce.product.infrastructure.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository jpaRepository;

    @Override
    public Product save(Product product) {
        return toModel(jpaRepository.save(toEntity(product)));
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<Product> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(this::toModel);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    @Override
    public PageResult<Product> findAll(ProductFilterCommand filter) {
        var direction = "asc".equalsIgnoreCase(filter.sortDir()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        var pageable = PageRequest.of(filter.page(), filter.size(), Sort.by(direction, filter.sortBy()));

        var page = jpaRepository.findFiltered(
                filter.categoryId(), filter.minPrice(), filter.maxPrice(), filter.search(), pageable);

        return new PageResult<>(
                page.getContent().stream().map(this::toModel).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ProductEntity toEntity(Product product) {
        var entity = new ProductEntity();
        entity.setId(product.id());
        entity.setName(product.name());
        entity.setSlug(product.slug());
        entity.setDescription(product.description());
        entity.setCategoryId(product.categoryId());
        entity.setBasePrice(product.basePrice());
        entity.setActive(product.active());
        entity.setImageUrl(product.imageUrl());
        entity.setCreatedAt(product.createdAt());
        entity.setUpdatedAt(product.updatedAt());
        return entity;
    }

    private Product toModel(ProductEntity entity) {
        return new Product(
                entity.getId(), entity.getName(), entity.getSlug(), entity.getDescription(),
                entity.getCategoryId(), entity.getBasePrice(), entity.isActive(),
                entity.getImageUrl(), entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }
}
