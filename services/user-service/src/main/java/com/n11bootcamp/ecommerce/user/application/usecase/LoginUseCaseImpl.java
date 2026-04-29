package com.n11bootcamp.ecommerce.user.application.usecase;

import com.n11bootcamp.ecommerce.user.application.dto.AuthTokensDto;
import com.n11bootcamp.ecommerce.user.application.dto.LoginCommand;
import com.n11bootcamp.ecommerce.user.application.port.in.LoginUseCase;
import com.n11bootcamp.ecommerce.user.application.port.out.PasswordEncoderPort;
import com.n11bootcamp.ecommerce.user.application.port.out.RefreshTokenRepositoryPort;
import com.n11bootcamp.ecommerce.user.application.port.out.TokenGeneratorPort;
import com.n11bootcamp.ecommerce.user.application.port.out.UserRepositoryPort;
import com.n11bootcamp.ecommerce.user.domain.exception.InvalidCredentialsException;
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
public class LoginUseCaseImpl implements LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;

    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    @Override
    @Transactional
    public AuthTokensDto execute(LoginCommand command) {
        var user = userRepository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        // Eski refresh tokenları iptal et
        refreshTokenRepository.revokeAllByUserId(user.id());

        var rawRefreshToken = UUID.randomUUID().toString();
        var tokenHash = sha256(rawRefreshToken);
        var expiresAt = Instant.now().plusMillis(refreshTokenExpirationMs);
        refreshTokenRepository.save(RefreshToken.create(user.id(), tokenHash, expiresAt));

        log.info("Kullanıcı giriş yaptı: userId={}", user.id());

        var accessToken = tokenGenerator.generateAccessToken(user.id(), user.role().name());
        return new AuthTokensDto(accessToken, rawRefreshToken, tokenGenerator.accessTokenExpiresInSeconds());
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
