package com.n11bootcamp.ecommerce.product.application.port.out;

import com.n11bootcamp.ecommerce.product.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {
    Category save(Category category);
    Optional<Category> findById(UUID id);
    Optional<Category> findBySlug(String slug);
    List<Category> findAll();
    boolean existsBySlug(String slug);
    void deleteById(UUID id);
}
