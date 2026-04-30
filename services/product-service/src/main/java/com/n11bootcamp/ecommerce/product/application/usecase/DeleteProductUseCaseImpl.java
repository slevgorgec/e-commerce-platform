package com.n11bootcamp.ecommerce.product.application.usecase;

import com.n11bootcamp.ecommerce.product.application.port.in.DeleteProductUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.ProductRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteProductUseCaseImpl implements DeleteProductUseCase {

    private final ProductRepositoryPort productRepository;

    @Override
    @Transactional
    public void execute(UUID id) {
        var existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        // Soft delete: ürünü pasife al
        var deactivated = existing.deactivate();
        productRepository.save(deactivated);
        log.info("Ürün pasife alındı: id={}", id);
    }
}
