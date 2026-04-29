package com.n11bootcamp.ecommerce.user.application.usecase;

import com.n11bootcamp.ecommerce.user.application.port.in.GetUserProfileUseCase;
import com.n11bootcamp.ecommerce.user.application.port.out.UserRepositoryPort;
import com.n11bootcamp.ecommerce.user.domain.exception.UserNotFoundException;
import com.n11bootcamp.ecommerce.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserProfileUseCaseImpl implements GetUserProfileUseCase {

    private final UserRepositoryPort userRepository;

    @Override
    @Transactional(readOnly = true)
    public User execute(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }
}
