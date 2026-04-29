package com.n11bootcamp.ecommerce.common.event.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Routing key: order.created
 * Producer: Order Service
 * Consumer(s): Product Service, Notification Service
 */
public record OrderCreatedEvent(
        String eventId,
        UUID orderId,
        UUID userId,
        List<OrderItem> items,
        BigDecimal totalAmount,
        Instant occurredAt
) {
    @JsonCreator
    public OrderCreatedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("userId") UUID userId,
            @JsonProperty("items") List<OrderItem> items,
            @JsonProperty("totalAmount") BigDecimal totalAmount,
            @JsonProperty("occurredAt") Instant occurredAt
    ) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.occurredAt = occurredAt;
    }

    public static OrderCreatedEvent of(UUID orderId, UUID userId, List<OrderItem> items, BigDecimal totalAmount) {
        return new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                orderId,
                userId,
                items,
                totalAmount,
                Instant.now()
        );
    }

    public record OrderItem(
            @JsonProperty("productId") UUID productId,
            @JsonProperty("variantId") UUID variantId,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("unitPrice") BigDecimal unitPrice
    ) {
        @JsonCreator
        public OrderItem {}
    }
}