package com.n11bootcamp.ecommerce.order.domain.exception;

/**
 * Tüm domain exception'larının base sınıfı.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}