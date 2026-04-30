package com.n11bootcamp.ecommerce.product.application.usecase;

import com.n11bootcamp.ecommerce.product.application.dto.CreateCategoryCommand;
import com.n11bootcamp.ecommerce.product.application.port.in.CreateCategoryUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.CategoryRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.exception.SlugAlreadyExistsException;
import com.n11bootcamp.ecommerce.product.domain.model.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCategoryUseCaseImpl implements CreateCategoryUseCase {

    private final CategoryRepositoryPort categoryRepository;

    @Override
    @Transactional
    public Category execute(CreateCategoryCommand command) {
        if (categoryRepository.existsBySlug(command.slug())) {
            throw new SlugAlreadyExistsException(command.slug());
        }
        var category = Category.create(command.name(), command.slug(), command.parentId());
        var saved = categoryRepository.save(category);
        log.info("Kategori oluşturuldu: id={}, slug={}", saved.id(), saved.slug());
        return saved;
    }
}
