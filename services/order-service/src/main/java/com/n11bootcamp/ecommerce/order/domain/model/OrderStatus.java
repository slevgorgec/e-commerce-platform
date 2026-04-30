package com.n11bootcamp.ecommerce.order.domain.model;

/**
 * Sipariş yaşam döngüsü durumları.
 * Saga Choreography akışı: PENDING → STOCK_RESERVED → PAYMENT_REQUESTED → CONFIRMED | CANCELLED
 */
public enum OrderStatus {
    PENDING,
    STOCK_RESERVED,
    PAYMENT_REQUESTED,
    CONFIRMED,
    CANCELLED
}