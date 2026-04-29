package com.n11bootcamp.ecommerce.user.application.port.out;

import com.n11bootcamp.ecommerce.user.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void revokeAllByUserId(java.util.UUID userId);
}
