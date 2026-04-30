package com.n11bootcamp.ecommerce.product.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductVariant(
        UUID id,
        UUID productId,
        String sku,
        String variantValue,
        BigDecimal price,
        int stock,
        int reservedStock
) {
    public static ProductVariant create(UUID productId, String sku, String variantValue,
                                         BigDecimal price, int stock) {
        return new ProductVariant(UUID.randomUUID(), productId, sku, variantValue, price, stock, 0);
    }

    public int availableStock() {
        return stock - reservedStock;
    }

    public ProductVariant reserve(int quantity) {
        if (availableStock() < quantity) {
            throw new IllegalStateException("Yetersiz stok");
        }
        return new ProductVariant(id, productId, sku, variantValue, price, stock, reservedStock + quantity);
    }

    public ProductVariant commitReservation(int quantity) {
        return new ProductVariant(id, productId, sku, variantValue, price, stock - quantity,
                reservedStock - quantity);
    }

    public ProductVariant releaseReservation(int quantity) {
        return new ProductVariant(id, productId, sku, variantValue, price, stock, reservedStock - quantity);
    }

    public ProductVariant updateStock(int newStock) {
        return new ProductVariant(id, productId, sku, variantValue, price, newStock, reservedStock);
    }

    public ProductVariant updatePrice(BigDecimal newPrice) {
        return new ProductVariant(id, productId, sku, variantValue, newPrice, stock, reservedStock);
    }
}
