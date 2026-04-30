package com.n11bootcamp.ecommerce.cart.application.usecase;

import com.n11bootcamp.ecommerce.cart.application.port.in.ClearCartUseCase;
import com.n11bootcamp.ecommerce.cart.application.port.out.CartRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClearCartUseCaseImpl implements ClearCartUseCase {

    private final CartRepositoryPort cartRepository;

    @Override
    public void execute(UUID userId) {
        cartRepository.deleteByUserId(userId);
        log.info("Sepet temizlendi: userId={}", userId);
    }
}
