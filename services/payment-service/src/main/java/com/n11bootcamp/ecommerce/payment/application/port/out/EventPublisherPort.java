package com.n11bootcamp.ecommerce.payment.application.port.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface EventPublisherPort {

    void publishPaymentCompleted(UUID orderReference, UUID userId, BigDecimal amount, String iyzicoPaymentId);

    void publishPaymentFailed(UUID orderReference, UUID userId, String failureReason);
}