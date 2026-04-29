package com.n11bootcamp.ecommerce.user.infrastructure.security;

import com.n11bootcamp.ecommerce.user.application.port.out.TokenGeneratorPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenGeneratorPort {

    private final JwtProperties jwtProperties;

    @Override
    public String generateAccessToken(UUID userId, String role) {
        var now = new Date();
        var expiry = new Date(now.getTime() + jwtProperties.getAccessTokenExpirationMs());

        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey())
                .compact();
    }

    @Override
    public long accessTokenExpiresInSeconds() {
        return jwtProperties.getAccessTokenExpirationMs() / 1000;
    }

    private SecretKey signingKey() {
        var keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
