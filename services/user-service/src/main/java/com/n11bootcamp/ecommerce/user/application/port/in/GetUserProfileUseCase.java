package com.n11bootcamp.ecommerce.user.application.port.in;

import com.n11bootcamp.ecommerce.user.domain.model.User;

import java.util.UUID;

public interface GetUserProfileUseCase {
    User execute(UUID userId);
}
