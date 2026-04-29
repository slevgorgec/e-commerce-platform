package com.n11bootcamp.ecommerce.user.application.usecase;

import com.n11bootcamp.ecommerce.user.application.dto.AuthTokensDto;
import com.n11bootcamp.ecommerce.user.application.dto.RegisterCommand;
import com.n11bootcamp.ecommerce.user.application.port.in.RegisterUserUseCase;
import com.n11bootcamp.ecommerce.user.application.port.out.*;
import com.n11bootcamp.ecommerce.user.domain.exception.EmailAlreadyExistsException;
import com.n11bootcamp.ecommerce.user.domain.model.RefreshToken;
import com.n11bootcamp.ecommerce.user.domain.model.User;
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
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    private final EventPublisherPort eventPublisher;

    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    @Override
    @Transactional
    public AuthTokensDto execute(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        var passwordHash = passwordEncoder.encode(command.password());
        var user = User.create(command.email(), passwordHash, command.firstName(), command.lastName());
        var savedUser = userRepository.save(user);

        log.info("Yeni kullanıcı kaydedildi: userId={}", savedUser.id());

        var rawRefreshToken = UUID.randomUUID().toString();
        var tokenHash = sha256(rawRefreshToken);
        var expiresAt = Instant.now().plusMillis(refreshTokenExpirationMs);
        refreshTokenRepository.save(RefreshToken.create(savedUser.id(), tokenHash, expiresAt));

        eventPublisher.publishUserRegistered(savedUser);

        var accessToken = tokenGenerator.generateAccessToken(savedUser.id(), savedUser.role().name());
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
