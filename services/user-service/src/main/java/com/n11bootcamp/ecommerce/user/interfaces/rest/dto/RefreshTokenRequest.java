package com.n11bootcamp.ecommerce.user.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank String refreshToken
) {}
