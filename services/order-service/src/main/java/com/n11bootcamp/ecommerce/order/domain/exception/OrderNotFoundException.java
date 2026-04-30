package com.n11bootcamp.ecommerce.order.domain.exception;

import java.util.UUID;

/**
 * Belirtilen ID'ye sahip sipariş bulunamadığında fırlatılır.
 */
public class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(UUID orderId) {
        super("Sipariş bulunamadı: id=" + orderId);
    }
}