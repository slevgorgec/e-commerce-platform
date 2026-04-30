package com.n11bootcamp.ecommerce.product.application.usecase;

import com.n11bootcamp.ecommerce.product.application.port.in.DeleteCategoryUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.CategoryRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteCategoryUseCaseImpl implements DeleteCategoryUseCase {

    private final CategoryRepositoryPort categoryRepository;

    @Override
    @Transactional
    public void execute(UUID id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
        log.info("Kategori silindi: id={}", id);
    }
}
