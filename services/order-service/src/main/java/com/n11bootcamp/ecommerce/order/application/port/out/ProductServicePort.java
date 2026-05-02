package com.n11bootcamp.ecommerce.order.application.port.out;

import com.n11bootcamp.ecommerce.order.application.dto.VariantInfo;

import java.util.UUID;

public interface ProductServicePort {

    VariantInfo getVariantInfo(UUID variantId);
}
