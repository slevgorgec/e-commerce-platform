package com.n11bootcamp.ecommerce.cart.application.port.out;

import com.n11bootcamp.ecommerce.cart.application.dto.VariantInfo;

import java.util.UUID;

public interface ProductServicePort {
    VariantInfo getVariantInfo(UUID variantId);
}
