package com.n11bootcamp.ecommerce.user.domain.exception;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(String identifier) {
        super("Kullanıcı bulunamadı: " + identifier);
    }
}
