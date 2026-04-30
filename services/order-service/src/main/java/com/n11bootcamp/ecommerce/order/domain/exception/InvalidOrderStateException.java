package com.n11bootcamp.ecommerce.order.domain.exception;

import com.n11bootcamp.ecommerce.order.domain.model.OrderStatus;

import java.util.UUID;

/**
 * Geçersiz sipariş durum geçişi denendiğinde fırlatılır.
 */
public class InvalidOrderStateException extends DomainException {

    public InvalidOrderStateException(UUID orderId, OrderStatus currentStatus, OrderStatus expectedStatus) {
        super(String.format("Geçersiz sipariş durumu: orderId=%s, mevcutDurum=%s, beklenenDurum=%s",
                orderId, currentStatus, expectedStatus));
    }

    public InvalidOrderStateException(String message) {
        super(message);
    }
}