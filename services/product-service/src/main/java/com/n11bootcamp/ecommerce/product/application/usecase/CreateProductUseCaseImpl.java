package com.n11bootcamp.ecommerce.product.application.usecase;

import com.n11bootcamp.ecommerce.product.application.dto.CreateProductCommand;
import com.n11bootcamp.ecommerce.product.application.port.in.CreateProductUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.ProductRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.exception.SlugAlreadyExistsException;
import com.n11bootcamp.ecommerce.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateProductUseCaseImpl implements CreateProductUseCase {

    private final ProductRepositoryPort productRepository;

    @Override
    @Transactional
    public Product execute(CreateProductCommand command) {
        if (productRepository.existsBySlug(command.slug())) {
            throw new SlugAlreadyExistsException(command.slug());
        }
        var product = Product.create(command.name(), command.slug(), command.description(),
                command.categoryId(), command.basePrice());
        var saved = productRepository.save(product);
        log.info("Ürün oluşturuldu: id={}, slug={}", saved.id(), saved.slug());
        return saved;
    }
}
