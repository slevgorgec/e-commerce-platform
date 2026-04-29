package com.n11bootcamp.ecommerce.user.application.dto;

public record UpdateProfileCommand(
        String firstName,
        String lastName
) {}
