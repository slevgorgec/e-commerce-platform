package com.n11bootcamp.ecommerce.user.unit;

import com.n11bootcamp.ecommerce.user.application.dto.RegisterCommand;
import com.n11bootcamp.ecommerce.user.application.port.out.*;
import com.n11bootcamp.ecommerce.user.application.usecase.RegisterUserUseCaseImpl;
import com.n11bootcamp.ecommerce.user.domain.exception.EmailAlreadyExistsException;
import com.n11bootcamp.ecommerce.user.domain.model.RefreshToken;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock private PasswordEncoderPort passwordEncoder;
    @Mock private TokenGeneratorPort tokenGenerator;
    @Mock private EventPublisherPort eventPublisher;

    @InjectMocks
    private RegisterUserUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(useCase, "refreshTokenExpirationMs", 604800000L);
    }

    @Test
    void execute_givenValidCommand_returnsTokens() {
        var command = new RegisterCommand("test@example.com", "password123", "Ali", "Veli");
        var savedUser = new User(UUID.randomUUID(), command.email(), "hashed", "Ali", "Veli",
                UserRole.USER, Instant.now(), null);

        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(passwordEncoder.encode(command.password())).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(savedUser);
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(tokenGenerator.generateAccessToken(any(), eq("USER"))).thenReturn("access-token");
        when(tokenGenerator.accessTokenExpiresInSeconds()).thenReturn(900L);

        var result = useCase.execute(command);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(result.accessTokenExpiresIn()).isEqualTo(900L);

        verify(eventPublisher).publishUserRegistered(savedUser);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void execute_givenDuplicateEmail_throwsEmailAlreadyExistsException() {
        var command = new RegisterCommand("existing@example.com", "password123", "Ali", "Veli");
        when(userRepository.existsByEmail(command.email())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("existing@example.com");

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishUserRegistered(any());
    }
}
