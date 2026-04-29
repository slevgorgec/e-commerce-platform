package com.n11bootcamp.ecommerce.user.application.dto;

public record LoginCommand(
        String email,
        String password
) {}
