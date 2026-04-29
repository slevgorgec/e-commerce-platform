package com.n11bootcamp.ecommerce.user.application.port.in;

import com.n11bootcamp.ecommerce.user.application.dto.AuthTokensDto;

public interface RefreshTokenUseCase {
    AuthTokensDto execute(String rawRefreshToken);
}
