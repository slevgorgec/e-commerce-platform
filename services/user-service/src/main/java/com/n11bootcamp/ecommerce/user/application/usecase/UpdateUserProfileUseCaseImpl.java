package com.n11bootcamp.ecommerce.user.application.usecase;

import com.n11bootcamp.ecommerce.user.application.dto.UpdateProfileCommand;
import com.n11bootcamp.ecommerce.user.application.port.in.UpdateUserProfileUseCase;
import com.n11bootcamp.ecommerce.user.application.port.out.UserRepositoryPort;
import com.n11bootcamp.ecommerce.user.domain.exception.UserNotFoundException;
import com.n11bootcamp.ecommerce.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateUserProfileUseCaseImpl implements UpdateUserProfileUseCase {

    private final UserRepositoryPort userRepository;

    @Override
    @Transactional
    public User execute(UUID userId, UpdateProfileCommand command) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        var updatedUser = user.withUpdatedProfile(command.firstName(), command.lastName());
        var saved = userRepository.save(updatedUser);

        log.info("Kullanıcı profili güncellendi: userId={}", userId);
        return saved;
    }
}
