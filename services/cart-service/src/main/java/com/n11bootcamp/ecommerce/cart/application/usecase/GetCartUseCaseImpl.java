package com.n11bootcamp.ecommerce.cart.application.usecase;

import com.n11bootcamp.ecommerce.cart.application.port.in.GetCartUseCase;
import com.n11bootcamp.ecommerce.cart.application.port.out.CartRepositoryPort;
import com.n11bootcamp.ecommerce.cart.domain.model.Cart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCartUseCaseImpl implements GetCartUseCase {

    private final CartRepositoryPort cartRepository;

    @Override
    public Cart execute(UUID userId) {
        var cart = cartRepository.findByUserId(userId)
                .orElse(Cart.empty(userId));
        log.debug("Sepet getirildi: userId={}, itemCount={}", userId, cart.items().size());
        return cart;
    }
}
