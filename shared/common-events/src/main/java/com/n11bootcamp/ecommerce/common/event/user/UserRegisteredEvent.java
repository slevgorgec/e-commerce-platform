package com.n11bootcamp.ecommerce.common.event.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

/**
 * Routing key: user.registered
 * Producer: User Service
 * Consumer(s): Notification Service
 */
public record UserRegisteredEvent(
        String eventId,
        UUID userId,
        String email,
        String firstName,
        String lastName,
        Instant occurredAt
) {
    @JsonCreator
    public UserRegisteredEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("userId") UUID userId,
            @JsonProperty("email") String email,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("occurredAt") Instant occurredAt
    ) {
        this.eventId = eventId;
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.occurredAt = occurredAt;
    }

    public static UserRegisteredEvent of(UUID userId, String email, String firstName, String lastName) {
        return new UserRegisteredEvent(
                UUID.randomUUID().toString(),
                userId,
                email,
                firstName,
                lastName,
                Instant.now()
        );
    }
}
