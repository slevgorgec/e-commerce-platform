package com.n11bootcamp.ecommerce.common.event.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

/**
 * Routing key: stock.committed
 * Producer: Product Service
 * Consumer(s): (none — informational)
 */
public record StockCommittedEvent(
        String eventId,
        UUID orderId,
        Instant occurredAt
) {
    @JsonCreator
    public StockCommittedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("occurredAt") Instant occurredAt
    ) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.occurredAt = occurredAt;
    }

    public static StockCommittedEvent of(UUID orderId) {
        return new StockCommittedEvent(UUID.randomUUID().toString(), orderId, Instant.now());
    }
}