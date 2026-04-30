package com.n11bootcamp.ecommerce.product.domain.exception;

public class InsufficientStockException extends DomainException {
    public InsufficientStockException(String sku, int available, int requested) {
        super("Yetersiz stok: sku=" + sku + ", mevcut=" + available + ", istenen=" + requested);
    }
}
