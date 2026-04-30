package com.n11bootcamp.ecommerce.cart.application.usecase;

import com.n11bootcamp.ecommerce.cart.application.dto.AddCartItemCommand;
import com.n11bootcamp.ecommerce.cart.application.port.in.AddCartItemUseCase;
import com.n11bootcamp.ecommerce.cart.application.port.out.CartRepositoryPort;
import com.n11bootcamp.ecommerce.cart.application.port.out.ProductServicePort;
import com.n11bootcamp.ecommerce.cart.domain.exception.ProductNotAvailableException;
import com.n11bootcamp.ecommerce.cart.domain.model.Cart;
import com.n11bootcamp.ecommerce.cart.domain.model.CartItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddCartItemUseCaseImpl implements AddCartItemUseCase {

    private final CartRepositoryPort cartRepository;
    private final ProductServicePort productService;

    @Override
    public Cart execute(AddCartItemCommand command) {
        var variantInfo = productService.getVariantInfo(command.variantId());

        if (!variantInfo.active()) {
            throw new ProductNotAvailableException(command.variantId());
        }

        var cart = cartRepository.findByUserId(command.userId())
                .orElse(Cart.empty(command.userId()));

        var newItem = CartItem.of(
                variantInfo.productId(),
                variantInfo.variantId(),
                variantInfo.productName(),
                variantInfo.variantValue(),
                variantInfo.price(),
                command.quantity()
        );

        var updatedCart = cart.addOrUpdateItem(newItem);
        var saved = cartRepository.save(updatedCart);

        log.info("Sepete ürün eklendi: userId={}, variantId={}, quantity={}",
                command.userId(), command.variantId(), command.quantity());
        return saved;
    }
}
