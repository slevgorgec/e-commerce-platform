package com.n11bootcamp.ecommerce.user.domain.model;

import java.time.Instant;
import java.util.UUID;

public record User(
        UUID id,
        String email,
        String passwordHash,
        String firstName,
        String lastName,
        UserRole role,
        Instant createdAt,
        Instant updatedAt
) {
    public static User create(String email, String passwordHash, String firstName, String lastName) {
        return new User(
                UUID.randomUUID(),
                email,
                passwordHash,
                firstName,
                lastName,
                UserRole.USER,
                Instant.now(),
                null
        );
    }

    public User withUpdatedProfile(String firstName, String lastName) {
        return new User(id, email, passwordHash, firstName, lastName, role, createdAt, Instant.now());
    }

    public User withRole(UserRole newRole) {
        return new User(id, email, passwordHash, firstName, lastName, newRole, createdAt, Instant.now());
    }
}
