package com.n11bootcamp.ecommerce.user.application.dto;

public record RegisterCommand(
        String email,
        String password,
        String firstName,
        String lastName
) {}
