package com.n11bootcamp.ecommerce.user.application.port.out;

import java.util.UUID;

public interface TokenGeneratorPort {
    String generateAccessToken(UUID userId, String role);
    long accessTokenExpiresInSeconds();
}
