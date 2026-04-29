package com.n11bootcamp.ecommerce.common.event.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

/**
 * Routing key: order.confirmed
 * Producer: Order Service
 * Consumer(s): Cart Service, Notification Service
 */
public record OrderConfirmedEvent(
        String eventId,
        UUID orderId,
        UUID userId,
        Instant occurredAt
) {
    @JsonCreator
    public OrderConfirmedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("userId") UUID userId,
            @JsonProperty("occurredAt") Instant occurredAt
    ) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.userId = userId;
        this.occurredAt = occurredAt;
    }

    public static OrderConfirmedEvent of(UUID orderId, UUID userId) {
        return new OrderConfirmedEvent(UUID.randomUUID().toString(), orderId, userId, Instant.now());
    }
}