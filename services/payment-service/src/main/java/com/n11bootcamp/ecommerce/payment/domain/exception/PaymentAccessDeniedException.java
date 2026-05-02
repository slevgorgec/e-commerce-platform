package com.n11bootcamp.ecommerce.payment.domain.exception;

import java.util.UUID;

public class PaymentAccessDeniedException extends DomainException {

    public PaymentAccessDeniedException(UUID orderReference) {
        super("Bu ödeme kaydına erişim yetkiniz yok: orderReference=" + orderReference);
    }
}
