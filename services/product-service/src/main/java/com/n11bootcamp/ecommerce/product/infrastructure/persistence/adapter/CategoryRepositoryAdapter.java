package com.n11bootcamp.ecommerce.product.infrastructure.persistence.adapter;

import com.n11bootcamp.ecommerce.product.application.port.out.CategoryRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.model.Category;
import com.n11bootcamp.ecommerce.product.infrastructure.persistence.entity.CategoryEntity;
import com.n11bootcamp.ecommerce.product.infrastructure.persistence.repository.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository jpaRepository;

    @Override
    public Category save(Category category) {
        return toModel(jpaRepository.save(toEntity(category)));
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(this::toModel);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream().map(this::toModel).toList();
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private CategoryEntity toEntity(Category category) {
        var entity = new CategoryEntity();
        entity.setId(category.id());
        entity.setName(category.name());
        entity.setSlug(category.slug());
        entity.setParentId(category.parentId());
        entity.setImageUrl(category.imageUrl());
        return entity;
    }

    private Category toModel(CategoryEntity entity) {
        return new Category(entity.getId(), entity.getName(), entity.getSlug(), entity.getParentId(), entity.getImageUrl());
    }
}
