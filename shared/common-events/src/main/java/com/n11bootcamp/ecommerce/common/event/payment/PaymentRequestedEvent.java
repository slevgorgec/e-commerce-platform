package com.n11bootcamp.ecommerce.common.event.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Routing key: payment.requested
 * Producer: Order Service
 * Consumer(s): Payment Service
 */
public record PaymentRequestedEvent(
        String eventId,
        UUID orderReference,
        UUID userId,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) {
    @JsonCreator
    public PaymentRequestedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("orderReference") UUID orderReference,
            @JsonProperty("userId") UUID userId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("currency") String currency,
            @JsonProperty("occurredAt") Instant occurredAt
    ) {
        this.eventId = eventId;
        this.orderReference = orderReference;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.occurredAt = occurredAt;
    }

    public static PaymentRequestedEvent of(UUID orderReference, UUID userId, BigDecimal amount) {
        return new PaymentRequestedEvent(
                UUID.randomUUID().toString(),
                orderReference,
                userId,
                amount,
                "TRY",
                Instant.now()
        );
    }
}