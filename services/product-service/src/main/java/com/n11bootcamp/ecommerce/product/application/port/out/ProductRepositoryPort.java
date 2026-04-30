package com.n11bootcamp.ecommerce.product.application.port.out;

import com.n11bootcamp.ecommerce.product.application.dto.PageResult;
import com.n11bootcamp.ecommerce.product.application.dto.ProductFilterCommand;
import com.n11bootcamp.ecommerce.product.domain.model.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    Optional<Product> findBySlug(String slug);
    boolean existsBySlug(String slug);
    PageResult<Product> findAll(ProductFilterCommand filter);
    void deleteById(UUID id);
}
