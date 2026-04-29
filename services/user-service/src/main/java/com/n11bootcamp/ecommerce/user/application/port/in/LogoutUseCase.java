package com.n11bootcamp.ecommerce.user.application.port.in;

public interface LogoutUseCase {
    void execute(String rawRefreshToken);
}
