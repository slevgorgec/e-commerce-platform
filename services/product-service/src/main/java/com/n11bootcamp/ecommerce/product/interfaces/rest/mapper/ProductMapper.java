package com.n11bootcamp.ecommerce.product.interfaces.rest.mapper;

import com.n11bootcamp.ecommerce.product.application.dto.*;
import com.n11bootcamp.ecommerce.product.domain.model.Category;
import com.n11bootcamp.ecommerce.product.domain.model.Product;
import com.n11bootcamp.ecommerce.product.domain.model.ProductVariant;
import com.n11bootcamp.ecommerce.product.interfaces.rest.dto.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductMapper {

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.id(), category.name(), category.slug(), category.parentId(), category.imageUrl());
    }

    public CreateCategoryCommand toCommand(CreateCategoryRequest request) {
        return new CreateCategoryCommand(request.name(), request.slug(), request.parentId(), request.imageUrl());
    }

    public UpdateCategoryCommand toCommand(UUID id, UpdateCategoryRequest request) {
        return new UpdateCategoryCommand(id, request.name(), request.slug(), request.imageUrl());
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.id(), product.name(), product.slug(), product.description(),
                product.categoryId(), product.basePrice(), product.active(),
                product.imageUrl(), product.createdAt(), product.updatedAt()
        );
    }

    public CreateProductCommand toCommand(CreateProductRequest request) {
        return new CreateProductCommand(request.name(), request.slug(), request.description(),
                request.categoryId(), request.basePrice(), request.imageUrl());
    }

    public UpdateProductCommand toCommand(UUID id, UpdateProductRequest request) {
        return new UpdateProductCommand(id, request.name(), request.slug(), request.description(),
                request.categoryId(), request.basePrice(), request.active(), request.imageUrl());
    }

    public AddVariantCommand toCommand(UUID productId, AddVariantRequest request) {
        return new AddVariantCommand(productId, request.sku(), request.variantValue(),
                request.price(), request.stock());
    }

    public VariantResponse toResponse(ProductVariant variant) {
        return new VariantResponse(
                variant.id(), variant.productId(), variant.sku(), variant.variantValue(),
                variant.price(), variant.stock(), variant.reservedStock(), variant.availableStock()
        );
    }
}
