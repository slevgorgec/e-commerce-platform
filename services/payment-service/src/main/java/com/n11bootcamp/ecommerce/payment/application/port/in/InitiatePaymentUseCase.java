package com.n11bootcamp.ecommerce.payment.application.port.in;

import com.n11bootcamp.ecommerce.payment.application.dto.InitiatePaymentCommand;
import com.n11bootcamp.ecommerce.payment.domain.model.Payment;

public interface InitiatePaymentUseCase {

    Payment execute(InitiatePaymentCommand command);
}
