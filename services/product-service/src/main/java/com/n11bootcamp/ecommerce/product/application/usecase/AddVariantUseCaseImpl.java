package com.n11bootcamp.ecommerce.product.application.usecase;

import com.n11bootcamp.ecommerce.product.application.dto.AddVariantCommand;
import com.n11bootcamp.ecommerce.product.application.port.in.AddVariantUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.ProductRepositoryPort;
import com.n11bootcamp.ecommerce.product.application.port.out.ProductVariantRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.exception.ProductNotFoundException;
import com.n11bootcamp.ecommerce.product.domain.exception.SlugAlreadyExistsException;
import com.n11bootcamp.ecommerce.product.domain.model.ProductVariant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddVariantUseCaseImpl implements AddVariantUseCase {

    private final ProductRepositoryPort productRepository;
    private final ProductVariantRepositoryPort variantRepository;

    @Override
    @Transactional
    public ProductVariant execute(AddVariantCommand command) {
        if (productRepository.findById(command.productId()).isEmpty()) {
            throw new ProductNotFoundException(command.productId());
        }
        if (variantRepository.existsBySku(command.sku())) {
            throw new SlugAlreadyExistsException("SKU zaten mevcut: " + command.sku());
        }
        var variant = ProductVariant.create(command.productId(), command.sku(),
                command.variantValue(), command.price(), command.stock());
        var saved = variantRepository.save(variant);
        log.info("Varyant eklendi: id={}, sku={}", saved.id(), saved.sku());
        return saved;
    }
}
