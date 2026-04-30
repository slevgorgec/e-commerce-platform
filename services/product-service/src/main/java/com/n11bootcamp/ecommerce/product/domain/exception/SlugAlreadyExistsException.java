package com.n11bootcamp.ecommerce.product.domain.exception;

public class SlugAlreadyExistsException extends DomainException {
    public SlugAlreadyExistsException(String slug) {
        super("Slug zaten kullanımda: " + slug);
    }
}
