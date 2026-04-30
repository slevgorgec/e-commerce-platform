package com.n11bootcamp.ecommerce.product.application.usecase;

import com.n11bootcamp.ecommerce.product.application.dto.UpdateProductCommand;
import com.n11bootcamp.ecommerce.product.application.port.in.UpdateProductUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.ProductRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.exception.ProductNotFoundException;
import com.n11bootcamp.ecommerce.product.domain.exception.SlugAlreadyExistsException;
import com.n11bootcamp.ecommerce.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProductUseCaseImpl implements UpdateProductUseCase {

    private final ProductRepositoryPort productRepository;

    @Override
    @Transactional
    public Product execute(UpdateProductCommand command) {
        var existing = productRepository.findById(command.id())
                .orElseThrow(() -> new ProductNotFoundException(command.id()));

        if (!existing.slug().equals(command.slug()) && productRepository.existsBySlug(command.slug())) {
            throw new SlugAlreadyExistsException(command.slug());
        }

        var updated = existing.update(command.name(), command.slug(), command.description(),
                command.categoryId(), command.basePrice(), command.active());
        var saved = productRepository.save(updated);
        log.info("Ürün güncellendi: id={}", saved.id());
        return saved;
    }
}
