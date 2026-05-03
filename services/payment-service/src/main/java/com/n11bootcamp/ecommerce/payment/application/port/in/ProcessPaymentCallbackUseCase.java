package com.n11bootcamp.ecommerce.payment.application.port.in;

import com.n11bootcamp.ecommerce.payment.domain.model.Payment;

public interface ProcessPaymentCallbackUseCase {

    Payment execute(String iyzicoToken);
}
