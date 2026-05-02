package com.n11bootcamp.ecommerce.product.application.usecase;

import com.n11bootcamp.ecommerce.product.application.dto.UpdateCategoryCommand;
import com.n11bootcamp.ecommerce.product.application.port.in.UpdateCategoryUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.CategoryRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.exception.CategoryNotFoundException;
import com.n11bootcamp.ecommerce.product.domain.exception.SlugAlreadyExistsException;
import com.n11bootcamp.ecommerce.product.domain.model.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCategoryUseCaseImpl implements UpdateCategoryUseCase {

    private final CategoryRepositoryPort categoryRepository;

    @Override
    @Transactional
    public Category execute(UpdateCategoryCommand command) {
        var existing = categoryRepository.findById(command.id())
                .orElseThrow(() -> new CategoryNotFoundException(command.id()));

        if (!existing.slug().equals(command.slug()) && categoryRepository.existsBySlug(command.slug())) {
            throw new SlugAlreadyExistsException(command.slug());
        }

        var updated = existing.withName(command.name(), command.slug(), command.imageUrl());
        var saved = categoryRepository.save(updated);
        log.info("Kategori güncellendi: id={}", saved.id());
        return saved;
    }
}
