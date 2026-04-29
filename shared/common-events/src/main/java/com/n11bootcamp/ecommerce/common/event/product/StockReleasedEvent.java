package com.n11bootcamp.ecommerce.common.event.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

/**
 * Routing key: stock.released
 * Producer: Product Service
 * Consumer(s): (none — informational)
 */
public record StockReleasedEvent(
        String eventId,
        UUID orderId,
        Instant occurredAt
) {
    @JsonCreator
    public StockReleasedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("occurredAt") Instant occurredAt
    ) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.occurredAt = occurredAt;
    }

    public static StockReleasedEvent of(UUID orderId) {
        return new StockReleasedEvent(UUID.randomUUID().toString(), orderId, Instant.now());
    }
}