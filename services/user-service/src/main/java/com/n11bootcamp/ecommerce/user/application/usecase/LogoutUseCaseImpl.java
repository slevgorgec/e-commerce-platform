package com.n11bootcamp.ecommerce.user.application.usecase;

import com.n11bootcamp.ecommerce.user.application.port.in.LogoutUseCase;
import com.n11bootcamp.ecommerce.user.application.port.out.RefreshTokenRepositoryPort;
import com.n11bootcamp.ecommerce.user.domain.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    @Override
    @Transactional
    public void execute(String rawRefreshToken) {
        var tokenHash = sha256(rawRefreshToken);
        var token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::new);

        refreshTokenRepository.save(token.revoke());
        log.info("Kullanıcı çıkış yaptı: userId={}", token.userId());
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
