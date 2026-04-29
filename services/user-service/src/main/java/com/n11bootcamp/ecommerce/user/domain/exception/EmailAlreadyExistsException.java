package com.n11bootcamp.ecommerce.user.domain.exception;

public class EmailAlreadyExistsException extends DomainException {

    public EmailAlreadyExistsException(String email) {
        super("Bu email adresi zaten kullanımda: " + email);
    }
}
