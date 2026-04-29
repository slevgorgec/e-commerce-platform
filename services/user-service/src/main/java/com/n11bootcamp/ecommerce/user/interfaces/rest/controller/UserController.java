package com.n11bootcamp.ecommerce.user.interfaces.rest.controller;

import com.n11bootcamp.ecommerce.user.application.port.in.GetUserProfileUseCase;
import com.n11bootcamp.ecommerce.user.application.port.in.UpdateUserProfileUseCase;
import com.n11bootcamp.ecommerce.user.interfaces.rest.dto.UpdateProfileRequest;
import com.n11bootcamp.ecommerce.user.interfaces.rest.dto.UserProfileResponse;
import com.n11bootcamp.ecommerce.user.interfaces.rest.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Users", description = "Kullanıcı profil yönetimi")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final UserMapper userMapper;

    @Operation(summary = "Kullanıcı profilini getir")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProfile(@PathVariable UUID id,
                                                          @RequestHeader("X-User-Id") String requesterId,
                                                          @RequestHeader("X-User-Role") String requesterRole) {
        // Kullanıcı sadece kendi profilini görebilir; ADMIN herkesi görebilir.
        if (!requesterId.equals(id.toString()) && !"ADMIN".equals(requesterRole)) {
            return ResponseEntity.status(403).body(Map.of("error", "Erişim reddedildi"));
        }

        var user = getUserProfileUseCase.execute(id);
        return ResponseEntity.ok(Map.of("data", userMapper.toResponse(user), "timestamp", Instant.now()));
    }

    @Operation(summary = "Kendi profilini getir")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyProfile(@RequestHeader("X-User-Id") String userId) {
        var user = getUserProfileUseCase.execute(UUID.fromString(userId));
        return ResponseEntity.ok(Map.of("data", userMapper.toResponse(user), "timestamp", Instant.now()));
    }

    @Operation(summary = "Kullanıcı profilini güncelle")
    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestHeader("X-User-Id") String userId,
                                                             @Valid @RequestBody UpdateProfileRequest request) {
        var updated = updateUserProfileUseCase.execute(UUID.fromString(userId), userMapper.toCommand(request));
        return ResponseEntity.ok(Map.of("data", userMapper.toResponse(updated), "timestamp", Instant.now()));
    }
}
