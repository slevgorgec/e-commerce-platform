package com.n11bootcamp.ecommerce.user.application.usecase;

import com.n11bootcamp.ecommerce.user.application.dto.AuthTokensDto;
import com.n11bootcamp.ecommerce.user.application.port.in.RefreshTokenUseCase;
import com.n11bootcamp.ecommerce.user.application.port.out.RefreshTokenRepositoryPort;
import com.n11bootcamp.ecommerce.user.application.port.out.TokenGeneratorPort;
import com.n11bootcamp.ecommerce.user.application.port.out.UserRepositoryPort;
import com.n11bootcamp.ecommerce.user.domain.exception.InvalidTokenException;
import com.n11bootcamp.ecommerce.user.domain.exception.UserNotFoundException;
import com.n11bootcamp.ecommerce.user.domain.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserRepositoryPort userRepository;
    private final TokenGeneratorPort tokenGenerator;

    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    @Override
    @Transactional
    public AuthTokensDto execute(String rawRefreshToken) {
        var tokenHash = sha256(rawRefreshToken);
        var existingToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::new);

        if (!existingToken.isValid()) {
            throw new InvalidTokenException();
        }

        var user = userRepository.findById(existingToken.userId())
                .orElseThrow(() -> new UserNotFoundException(existingToken.userId().toString()));

        // Eski token'ı iptal et, yeni token üret (rotation)
        refreshTokenRepository.save(existingToken.revoke());

        var newRawToken = UUID.randomUUID().toString();
        var newTokenHash = sha256(newRawToken);
        var expiresAt = Instant.now().plusMillis(refreshTokenExpirationMs);
        refreshTokenRepository.save(RefreshToken.create(user.id(), newTokenHash, expiresAt));

        log.debug("Refresh token döndürüldü: userId={}", user.id());

        var accessToken = tokenGenerator.generateAccessToken(user.id(), user.role().name());
        return new AuthTokensDto(accessToken, newRawToken, tokenGenerator.accessTokenExpiresInSeconds());
    }

    private String sha256(String input) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algoritması bulunamadı", e);
        }
    }
}
