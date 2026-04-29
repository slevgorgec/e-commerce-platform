package com.n11bootcamp.ecommerce.user.infrastructure.persistence.adapter;

import com.n11bootcamp.ecommerce.user.application.port.out.RefreshTokenRepositoryPort;
import com.n11bootcamp.ecommerce.user.domain.model.RefreshToken;
import com.n11bootcamp.ecommerce.user.infrastructure.persistence.entity.RefreshTokenEntity;
import com.n11bootcamp.ecommerce.user.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository jpaRepository;

    @Override
    public RefreshToken save(RefreshToken token) {
        return toModel(jpaRepository.save(toEntity(token)));
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toModel);
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        jpaRepository.revokeAllByUserId(userId);
    }

    private RefreshTokenEntity toEntity(RefreshToken token) {
        var entity = new RefreshTokenEntity();
        entity.setId(token.id());
        entity.setUserId(token.userId());
        entity.setTokenHash(token.tokenHash());
        entity.setExpiresAt(token.expiresAt());
        entity.setRevoked(token.revoked());
        entity.setCreatedAt(token.createdAt());
        return entity;
    }

    private RefreshToken toModel(RefreshTokenEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.isRevoked(),
                entity.getCreatedAt()
        );
    }
}
