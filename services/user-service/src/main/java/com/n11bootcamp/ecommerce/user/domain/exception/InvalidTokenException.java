package com.n11bootcamp.ecommerce.user.domain.exception;

public class InvalidTokenException extends DomainException {

    public InvalidTokenException() {
        super("Geçersiz veya süresi dolmuş token");
    }
}
