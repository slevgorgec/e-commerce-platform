package com.n11bootcamp.ecommerce.common.event.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

/**
 * Routing key: order.cancelled
 * Producer: Order Service
 * Consumer(s): Product Service, Notification Service
 */
public record OrderCancelledEvent(
        String eventId,
        UUID orderId,
        UUID userId,
        String reason,
        Instant occurredAt
) {
    @JsonCreator
    public OrderCancelledEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("userId") UUID userId,
            @JsonProperty("reason") String reason,
            @JsonProperty("occurredAt") Instant occurredAt
    ) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.userId = userId;
        this.reason = reason;
        this.occurredAt = occurredAt;
    }

    public static OrderCancelledEvent of(UUID orderId, UUID userId, String reason) {
        return new OrderCancelledEvent(UUID.randomUUID().toString(), orderId, userId, reason, Instant.now());
    }
}