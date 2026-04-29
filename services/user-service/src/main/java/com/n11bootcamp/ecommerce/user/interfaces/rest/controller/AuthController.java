package com.n11bootcamp.ecommerce.user.interfaces.rest.controller;

import com.n11bootcamp.ecommerce.user.application.dto.LoginCommand;
import com.n11bootcamp.ecommerce.user.application.port.in.*;
import com.n11bootcamp.ecommerce.user.interfaces.rest.dto.*;
import com.n11bootcamp.ecommerce.user.interfaces.rest.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@Tag(name = "Authentication", description = "Kullanıcı kayıt ve giriş işlemleri")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final UserMapper userMapper;

    @Operation(summary = "Yeni kullanıcı kaydı")
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        var tokens = registerUserUseCase.execute(userMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", userMapper.toAuthResponse(tokens), "timestamp", Instant.now()));
    }

    @Operation(summary = "Kullanıcı girişi")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        var tokens = loginUseCase.execute(new LoginCommand(request.email(), request.password()));
        return ResponseEntity.ok(Map.of("data", userMapper.toAuthResponse(tokens), "timestamp", Instant.now()));
    }

    @Operation(summary = "Access token yenileme")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        var tokens = refreshTokenUseCase.execute(request.refreshToken());
        return ResponseEntity.ok(Map.of("data", userMapper.toAuthResponse(tokens), "timestamp", Instant.now()));
    }

    @Operation(summary = "Çıkış — refresh token iptal edilir")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@Valid @RequestBody LogoutRequest request) {
        logoutUseCase.execute(request.refreshToken());
        return ResponseEntity.ok(Map.of("data", Map.of("message", "Başarıyla çıkış yapıldı"), "timestamp", Instant.now()));
    }
}
