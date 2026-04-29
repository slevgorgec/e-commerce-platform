package com.n11bootcamp.ecommerce.user.application.dto;

public record AuthTokensDto(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn
) {}
