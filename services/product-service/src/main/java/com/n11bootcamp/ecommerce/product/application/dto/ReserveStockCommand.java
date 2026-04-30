package com.n11bootcamp.ecommerce.product.application.dto;

import java.util.List;
import java.util.UUID;

public record ReserveStockCommand(UUID orderId, List<Item> items) {
    public record Item(UUID variantId, int quantity) {}
}
