package com.n11bootcamp.ecommerce.common.event.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

/**
 * Routing key: payment.failed
 * Producer: Payment Service
 * Consumer(s): Order Service, Product Service, Notification Service
 */
public record PaymentFailedEvent(
        String eventId,
        UUID orderReference,
        UUID userId,
        String failureReason,
        Instant occurredAt
) {
    @JsonCreator
    public PaymentFailedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("orderReference") UUID orderReference,
            @JsonProperty("userId") UUID userId,
            @JsonProperty("failureReason") String failureReason,
            @JsonProperty("occurredAt") Instant occurredAt
    ) {
        this.eventId = eventId;
        this.orderReference = orderReference;
        this.userId = userId;
        this.failureReason = failureReason;
        this.occurredAt = occurredAt;
    }

    public static PaymentFailedEvent of(UUID orderReference, UUID userId, String failureReason) {
        return new PaymentFailedEvent(UUID.randomUUID().toString(), orderReference, userId, failureReason, Instant.now());
    }
}