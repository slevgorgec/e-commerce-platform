package com.n11bootcamp.ecommerce.cart.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Cart(
        UUID userId,
        List<CartItem> items,
        Instant updatedAt
) {
    public static Cart empty(UUID userId) {
        return new Cart(userId, new ArrayList<>(), Instant.now());
    }

    public Cart addOrUpdateItem(CartItem newItem) {
        var updated = new ArrayList<CartItem>();
        boolean found = false;
        for (var item : items) {
            if (item.variantId().equals(newItem.variantId())) {
                updated.add(item.withQuantity(item.quantity() + newItem.quantity()));
                found = true;
            } else {
                updated.add(item);
            }
        }
        if (!found) {
            updated.add(newItem);
        }
        return new Cart(userId, updated, Instant.now());
    }

    public Cart updateItemQuantity(UUID variantId, int quantity) {
        var updated = items.stream()
                .map(item -> item.variantId().equals(variantId) ? item.withQuantity(quantity) : item)
                .toList();
        return new Cart(userId, updated, Instant.now());
    }

    public Cart removeItem(UUID variantId) {
        var updated = items.stream()
                .filter(item -> !item.variantId().equals(variantId))
                .toList();
        return new Cart(userId, updated, Instant.now());
    }

    public boolean containsVariant(UUID variantId) {
        return items.stream().anyMatch(item -> item.variantId().equals(variantId));
    }
}
