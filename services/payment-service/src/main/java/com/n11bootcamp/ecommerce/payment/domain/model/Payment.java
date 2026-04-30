package com.n11bootcamp.ecommerce.payment.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Payment(
        UUID id,
        UUID orderReference,
        UUID userId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String iyzicoPaymentId,
        String iyzicoResponse,
        Instant createdAt,
        Instant updatedAt
) {

    public static Payment create(UUID orderReference, UUID userId, BigDecimal amount) {
        return new Payment(
                UUID.randomUUID(),
                orderReference,
                userId,
                amount,
                "TRY",
                PaymentStatus.PENDING,
                null,
                null,
                Instant.now(),
                null
        );
    }

    public Payment withCompleted(String iyzicoPaymentId, String iyzicoResponse) {
        return new Payment(id, orderReference, userId, amount, currency,
                PaymentStatus.COMPLETED, iyzicoPaymentId, iyzicoResponse, createdAt, Instant.now());
    }

    public Payment withFailed(String failureReason) {
        return new Payment(id, orderReference, userId, amount, currency,
                PaymentStatus.FAILED, null, failureReason, createdAt, Instant.now());
    }
}
