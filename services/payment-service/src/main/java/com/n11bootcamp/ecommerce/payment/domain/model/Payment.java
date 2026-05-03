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
        String checkoutFormUrl,
        String iyzicoToken,
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
                null,
                null,
                Instant.now(),
                null
        );
    }

    public Payment withCheckoutInitiated(String checkoutFormUrl, String iyzicoToken) {
        return new Payment(id, orderReference, userId, amount, currency,
                PaymentStatus.PENDING, null, null, checkoutFormUrl, iyzicoToken, createdAt, Instant.now());
    }

    public Payment withCompleted(String iyzicoPaymentId, String iyzicoResponse) {
        return new Payment(id, orderReference, userId, amount, currency,
                PaymentStatus.COMPLETED, iyzicoPaymentId, iyzicoResponse, checkoutFormUrl, iyzicoToken, createdAt, Instant.now());
    }

    public Payment withFailed(String failureReason) {
        var jsonReason = "{\"error\":\"" + (failureReason != null ? failureReason.replace("\"", "\\\"") : "unknown") + "\"}";
        return new Payment(id, orderReference, userId, amount, currency,
                PaymentStatus.FAILED, null, jsonReason, checkoutFormUrl, iyzicoToken, createdAt, Instant.now());
    }
}
