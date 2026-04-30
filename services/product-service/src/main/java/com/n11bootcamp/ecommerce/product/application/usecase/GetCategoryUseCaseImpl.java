package com.n11bootcamp.ecommerce.product.application.usecase;

import com.n11bootcamp.ecommerce.product.application.port.in.GetCategoryUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.CategoryRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.exception.CategoryNotFoundException;
import com.n11bootcamp.ecommerce.product.domain.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCategoryUseCaseImpl implements GetCategoryUseCase {

    private final CategoryRepositoryPort categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Category getById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
}
