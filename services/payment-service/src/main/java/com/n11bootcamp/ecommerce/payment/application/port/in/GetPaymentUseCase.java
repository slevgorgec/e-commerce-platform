package com.n11bootcamp.ecommerce.payment.application.port.in;

import com.n11bootcamp.ecommerce.payment.domain.model.Payment;

import java.util.UUID;

public interface GetPaymentUseCase {

    Payment getByOrderReference(UUID orderReference, UUID requesterId);
}
