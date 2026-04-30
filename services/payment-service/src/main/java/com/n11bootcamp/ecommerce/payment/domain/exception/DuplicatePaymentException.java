package com.n11bootcamp.ecommerce.payment.domain.exception;

import java.util.UUID;

public class DuplicatePaymentException extends DomainException {

    public DuplicatePaymentException(UUID orderReference) {
        super("Bu sipariş için zaten bir ödeme başlatıldı: orderReference=" + orderReference);
    }
}