package com.n11bootcamp.ecommerce.payment.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderReference,
        UUID userId,
        BigDecimal amount,
        String currency,
        String status,
        String iyzicoPaymentId,
        Instant createdAt,
        Instant updatedAt
) {}
