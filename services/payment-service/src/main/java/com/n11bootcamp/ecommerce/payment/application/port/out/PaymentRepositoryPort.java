package com.n11bootcamp.ecommerce.payment.application.port.out;

import com.n11bootcamp.ecommerce.payment.domain.model.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryPort {

    Payment save(Payment payment);

    Optional<Payment> findByOrderReference(UUID orderReference);

    Optional<Payment> findByIyzicoToken(String iyzicoToken);

    boolean existsByOrderReference(UUID orderReference);
}
