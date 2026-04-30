package com.n11bootcamp.ecommerce.product.application.usecase;

import com.n11bootcamp.ecommerce.product.application.port.in.GetProductUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.ProductRepositoryPort;
import com.n11bootcamp.ecommerce.product.application.port.out.ProductVariantRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.exception.ProductNotFoundException;
import com.n11bootcamp.ecommerce.product.domain.exception.VariantNotFoundException;
import com.n11bootcamp.ecommerce.product.domain.model.Product;
import com.n11bootcamp.ecommerce.product.domain.model.ProductVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetProductUseCaseImpl implements GetProductUseCase {

    private final ProductRepositoryPort productRepository;
    private final ProductVariantRepositoryPort variantRepository;

    @Override
    @Transactional(readOnly = true)
    public Product getById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Product getBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new ProductNotFoundException(slug));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariant> getVariantsByProductId(UUID productId) {
        if (productRepository.findById(productId).isEmpty()) {
            throw new ProductNotFoundException(productId);
        }
        return variantRepository.findByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariant getVariantById(UUID variantId) {
        return variantRepository.findById(variantId)
                .orElseThrow(() -> new VariantNotFoundException(variantId));
    }
}
