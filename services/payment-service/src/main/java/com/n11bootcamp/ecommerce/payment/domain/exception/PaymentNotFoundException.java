package com.n11bootcamp.ecommerce.payment.domain.exception;

import java.util.UUID;

public class PaymentNotFoundException extends DomainException {

    public PaymentNotFoundException(UUID orderReference) {
        super("Ödeme bulunamadı: orderReference=" + orderReference);
    }
}