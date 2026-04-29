package com.n11bootcamp.ecommerce.user.unit;

import com.n11bootcamp.ecommerce.user.application.dto.LoginCommand;
import com.n11bootcamp.ecommerce.user.application.port.out.*;
import com.n11bootcamp.ecommerce.user.application.usecase.LoginUseCaseImpl;
import com.n11bootcamp.ecommerce.user.domain.exception.InvalidCredentialsException;
import com.n11bootcamp.ecommerce.user.domain.model.User;
import com.n11bootcamp.ecommerce.user.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock private PasswordEncoderPort passwordEncoder;
    @Mock private TokenGeneratorPort tokenGenerator;

    @InjectMocks
    private LoginUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(useCase, "refreshTokenExpirationMs", 604800000L);
    }

    @Test
    void execute_givenValidCredentials_returnsTokens() {
        var user = new User(UUID.randomUUID(), "user@example.com", "hashed", "Ali", "Veli",
                UserRole.USER, Instant.now(), null);
        var command = new LoginCommand("user@example.com", "password123");

        when(userRepository.findByEmail(command.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(command.password(), "hashed")).thenReturn(true);
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(tokenGenerator.generateAccessToken(user.id(), "USER")).thenReturn("access-token");
        when(tokenGenerator.accessTokenExpiresInSeconds()).thenReturn(900L);

        var result = useCase.execute(command);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isNotBlank();
        verify(refreshTokenRepository).revokeAllByUserId(user.id());
    }

    @Test
    void execute_givenWrongPassword_throwsInvalidCredentialsException() {
        var user = new User(UUID.randomUUID(), "user@example.com", "hashed", "Ali", "Veli",
                UserRole.USER, Instant.now(), null);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(new LoginCommand("user@example.com", "wrongpass")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void execute_givenUnknownEmail_throwsInvalidCredentialsException() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new LoginCommand("notfound@example.com", "pass")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
