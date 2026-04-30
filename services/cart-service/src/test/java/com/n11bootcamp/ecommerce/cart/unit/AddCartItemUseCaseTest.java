package com.n11bootcamp.ecommerce.cart.unit;

import com.n11bootcamp.ecommerce.cart.application.dto.AddCartItemCommand;
import com.n11bootcamp.ecommerce.cart.application.dto.VariantInfo;
import com.n11bootcamp.ecommerce.cart.application.port.out.CartRepositoryPort;
import com.n11bootcamp.ecommerce.cart.application.port.out.ProductServicePort;
import com.n11bootcamp.ecommerce.cart.application.usecase.AddCartItemUseCaseImpl;
import com.n11bootcamp.ecommerce.cart.domain.exception.ProductNotAvailableException;
import com.n11bootcamp.ecommerce.cart.domain.model.Cart;
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
class AddCartItemUseCaseTest {

    @Mock
    private CartRepositoryPort cartRepository;

    @Mock
    private ProductServicePort productService;

    @InjectMocks
    private AddCartItemUseCaseImpl useCase;

    @Test
    void execute_givenValidCommand_addsItemToCart() {
        var userId = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var productId = UUID.randomUUID();

        var command = new AddCartItemCommand(userId, productId, variantId, 2);
        var variantInfo = new VariantInfo(productId, variantId, "Test Ürün", "M",
                new BigDecimal("99.90"), true);

        when(productService.getVariantInfo(variantId)).thenReturn(variantInfo);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(command);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().variantId()).isEqualTo(variantId);
        assertThat(result.items().getFirst().quantity()).isEqualTo(2);
        assertThat(result.items().getFirst().priceSnapshot()).isEqualByComparingTo("99.90");
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void execute_givenExistingItem_incrementsQuantity() {
        var userId = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var productId = UUID.randomUUID();

        var existingCart = Cart.empty(userId);
        var variantInfo = new VariantInfo(productId, variantId, "Test Ürün", "M",
                new BigDecimal("99.90"), true);
        existingCart = existingCart.addOrUpdateItem(
                com.n11bootcamp.ecommerce.cart.domain.model.CartItem.of(
                        productId, variantId, "Test Ürün", "M", new BigDecimal("99.90"), 1));

        var command = new AddCartItemCommand(userId, productId, variantId, 2);

        when(productService.getVariantInfo(variantId)).thenReturn(variantInfo);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(command);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().quantity()).isEqualTo(3);
    }

    @Test
    void execute_givenInactiveProduct_throwsProductNotAvailableException() {
        var userId = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var productId = UUID.randomUUID();

        var command = new AddCartItemCommand(userId, productId, variantId, 1);
        var variantInfo = new VariantInfo(productId, variantId, "Pasif Ürün", "M",
                new BigDecimal("50.00"), false);

        when(productService.getVariantInfo(variantId)).thenReturn(variantInfo);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(ProductNotAvailableException.class);

        verify(cartRepository, never()).save(any());
    }
}