package com.n11bootcamp.ecommerce.common.event.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Routing key: payment.completed
 * Producer: Payment Service
 * Consumer(s): Order Service, Product Service, Notification Service
 */
public record PaymentCompletedEvent(
        String eventId,
        UUID orderReference,
        UUID userId,
        BigDecimal amount,
        String iyzicoPaymentId,
        Instant occurredAt
) {
    @JsonCreator
    public PaymentCompletedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("orderReference") UUID orderReference,
            @JsonProperty("userId") UUID userId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("iyzicoPaymentId") String iyzicoPaymentId,
            @JsonProperty("occurredAt") Instant occurredAt
    ) {
        this.eventId = eventId;
        this.orderReference = orderReference;
        this.userId = userId;
        this.amount = amount;
        this.iyzicoPaymentId = iyzicoPaymentId;
        this.occurredAt = occurredAt;
    }

    public static PaymentCompletedEvent of(UUID orderReference, UUID userId, BigDecimal amount, String iyzicoPaymentId) {
        return new PaymentCompletedEvent(
                UUID.randomUUID().toString(),
                orderReference,
                userId,
                amount,
                iyzicoPaymentId,
                Instant.now()
        );
    }
}