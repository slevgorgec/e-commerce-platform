package com.n11bootcamp.ecommerce.product.application.port.in;

import com.n11bootcamp.ecommerce.product.domain.model.Product;
import com.n11bootcamp.ecommerce.product.domain.model.ProductVariant;

import java.util.List;
import java.util.UUID;

public interface GetProductUseCase {
    Product getById(UUID id);
    Product getBySlug(String slug);
    List<ProductVariant> getVariantsByProductId(UUID productId);
    ProductVariant getVariantById(UUID variantId);
}
