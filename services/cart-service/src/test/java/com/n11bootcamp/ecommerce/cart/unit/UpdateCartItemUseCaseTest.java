package com.n11bootcamp.ecommerce.cart.unit;

import com.n11bootcamp.ecommerce.cart.application.dto.UpdateCartItemCommand;
import com.n11bootcamp.ecommerce.cart.application.port.out.CartRepositoryPort;
import com.n11bootcamp.ecommerce.cart.application.usecase.UpdateCartItemUseCaseImpl;
import com.n11bootcamp.ecommerce.cart.domain.exception.CartItemNotFoundException;
import com.n11bootcamp.ecommerce.cart.domain.model.Cart;
import com.n11bootcamp.ecommerce.cart.domain.model.CartItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCartItemUseCaseTest {

    @Mock
    private CartRepositoryPort cartRepository;

    @InjectMocks
    private UpdateCartItemUseCaseImpl useCase;

    @Test
    void execute_givenExistingItem_updatesQuantity() {
        var userId = UUID.randomUUID();
        var variantId = UUID.randomUUID();

        var cart = Cart.empty(userId).addOrUpdateItem(
                CartItem.of(UUID.randomUUID(), variantId, "Ürün", "L",
                        new BigDecimal("150.00"), 1));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(new UpdateCartItemCommand(userId, variantId, 5));

        assertThat(result.items().getFirst().quantity()).isEqualTo(5);
    }

    @Test
    void execute_givenNonExistingItem_throwsCartItemNotFoundException() {
        var userId = UUID.randomUUID();
        var variantId = UUID.randomUUID();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(Cart.empty(userId)));

        assertThatThrownBy(() -> useCase.execute(new UpdateCartItemCommand(userId, variantId, 3)))
                .isInstanceOf(CartItemNotFoundException.class);

        verify(cartRepository, never()).save(any());
    }
}