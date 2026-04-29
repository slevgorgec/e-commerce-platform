package com.n11bootcamp.ecommerce.user.domain.exception;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Geçersiz email veya şifre");
    }
}
