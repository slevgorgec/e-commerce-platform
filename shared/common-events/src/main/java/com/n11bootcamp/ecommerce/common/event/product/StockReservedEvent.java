package com.n11bootcamp.ecommerce.common.event.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

/**
 * Routing key: stock.reserved
 * Producer: Product Service
 * Consumer(s): Order Service
 */
public record StockReservedEvent(
        String eventId,
        UUID orderId,
        Instant occurredAt
) {
    @JsonCreator
    public StockReservedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("occurredAt") Instant occurredAt
    ) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.occurredAt = occurredAt;
    }

    public static StockReservedEvent of(UUID orderId) {
        return new StockReservedEvent(UUID.randomUUID().toString(), orderId, Instant.now());
    }
}