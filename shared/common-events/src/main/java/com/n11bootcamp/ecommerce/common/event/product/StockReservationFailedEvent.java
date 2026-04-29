package com.n11bootcamp.ecommerce.common.event.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

/**
 * Routing key: stock.reservation.failed
 * Producer: Product Service
 * Consumer(s): Order Service
 */
public record StockReservationFailedEvent(
        String eventId,
        UUID orderId,
        String reason,
        Instant occurredAt
) {
    @JsonCreator
    public StockReservationFailedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("reason") String reason,
            @JsonProperty("occurredAt") Instant occurredAt
    ) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.reason = reason;
        this.occurredAt = occurredAt;
    }

    public static StockReservationFailedEvent of(UUID orderId, String reason) {
        return new StockReservationFailedEvent(UUID.randomUUID().toString(), orderId, reason, Instant.now());
    }
}