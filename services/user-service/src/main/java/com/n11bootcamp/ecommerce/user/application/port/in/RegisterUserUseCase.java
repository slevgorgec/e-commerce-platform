package com.n11bootcamp.ecommerce.user.application.port.in;

import com.n11bootcamp.ecommerce.user.application.dto.AuthTokensDto;
import com.n11bootcamp.ecommerce.user.application.dto.RegisterCommand;

public interface RegisterUserUseCase {
    AuthTokensDto execute(RegisterCommand command);
}
