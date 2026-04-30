package com.n11bootcamp.ecommerce.cart.interfaces.rest.mapper;

import com.n11bootcamp.ecommerce.cart.domain.model.Cart;
import com.n11bootcamp.ecommerce.cart.domain.model.CartItem;
import com.n11bootcamp.ecommerce.cart.interfaces.rest.dto.CartItemResponse;
import com.n11bootcamp.ecommerce.cart.interfaces.rest.dto.CartResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {
        var itemResponses = cart.items().stream()
                .map(this::toItemResponse)
                .toList();

        var totalAmount = itemResponses.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalItems = cart.items().stream()
                .mapToInt(CartItem::quantity)
                .sum();

        return new CartResponse(
                cart.userId(),
                itemResponses,
                totalAmount,
                totalItems,
                cart.updatedAt()
        );
    }

    private CartItemResponse toItemResponse(CartItem item) {
        var lineTotal = item.priceSnapshot().multiply(BigDecimal.valueOf(item.quantity()));
        return new CartItemResponse(
                item.productId(),
                item.variantId(),
                item.productName(),
                item.variantValue(),
                item.priceSnapshot(),
                item.quantity(),
                lineTotal,
                item.addedAt()
        );
    }
}
