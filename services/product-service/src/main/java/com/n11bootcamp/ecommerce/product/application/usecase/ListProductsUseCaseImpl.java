package com.n11bootcamp.ecommerce.product.application.usecase;

import com.n11bootcamp.ecommerce.product.application.dto.PageResult;
import com.n11bootcamp.ecommerce.product.application.dto.ProductFilterCommand;
import com.n11bootcamp.ecommerce.product.application.port.in.ListProductsUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.ProductRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListProductsUseCaseImpl implements ListProductsUseCase {

    private final ProductRepositoryPort productRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<Product> execute(ProductFilterCommand filter) {
        return productRepository.findAll(filter);
    }
}
