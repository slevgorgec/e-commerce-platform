package com.n11bootcamp.ecommerce.cart.application.usecase;

import com.n11bootcamp.ecommerce.cart.application.dto.UpdateCartItemCommand;
import com.n11bootcamp.ecommerce.cart.application.port.in.UpdateCartItemUseCase;
import com.n11bootcamp.ecommerce.cart.application.port.out.CartRepositoryPort;
import com.n11bootcamp.ecommerce.cart.domain.exception.CartItemNotFoundException;
import com.n11bootcamp.ecommerce.cart.domain.model.Cart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCartItemUseCaseImpl implements UpdateCartItemUseCase {

    private final CartRepositoryPort cartRepository;

    @Override
    public Cart execute(UpdateCartItemCommand command) {
        var cart = cartRepository.findByUserId(command.userId())
                .orElse(Cart.empty(command.userId()));

        if (!cart.containsVariant(command.variantId())) {
            throw new CartItemNotFoundException(command.variantId());
        }

        var updatedCart = cart.updateItemQuantity(command.variantId(), command.quantity());
        var saved = cartRepository.save(updatedCart);

        log.info("Sepet ürün adedi güncellendi: userId={}, variantId={}, quantity={}",
                command.userId(), command.variantId(), command.quantity());
        return saved;
    }
}
