package com.n11bootcamp.ecommerce.user.application.port.in;

import com.n11bootcamp.ecommerce.user.application.dto.AuthTokensDto;
import com.n11bootcamp.ecommerce.user.application.dto.LoginCommand;

public interface LoginUseCase {
    AuthTokensDto execute(LoginCommand command);
}
