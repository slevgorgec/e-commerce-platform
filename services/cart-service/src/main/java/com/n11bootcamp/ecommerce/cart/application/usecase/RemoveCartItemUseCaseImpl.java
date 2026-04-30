package com.n11bootcamp.ecommerce.cart.application.usecase;

import com.n11bootcamp.ecommerce.cart.application.port.in.RemoveCartItemUseCase;
import com.n11bootcamp.ecommerce.cart.application.port.out.CartRepositoryPort;
import com.n11bootcamp.ecommerce.cart.domain.exception.CartItemNotFoundException;
import com.n11bootcamp.ecommerce.cart.domain.model.Cart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoveCartItemUseCaseImpl implements RemoveCartItemUseCase {

    private final CartRepositoryPort cartRepository;

    @Override
    public Cart execute(UUID userId, UUID variantId) {
        var cart = cartRepository.findByUserId(userId)
                .orElse(Cart.empty(userId));

        if (!cart.containsVariant(variantId)) {
            throw new CartItemNotFoundException(variantId);
        }

        var updatedCart = cart.removeItem(variantId);
        var saved = cartRepository.save(updatedCart);

        log.info("Sepetten ürün silindi: userId={}, variantId={}", userId, variantId);
        return saved;
    }
}
