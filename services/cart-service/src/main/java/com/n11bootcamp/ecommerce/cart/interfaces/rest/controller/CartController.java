package com.n11bootcamp.ecommerce.cart.interfaces.rest.controller;

import com.n11bootcamp.ecommerce.cart.application.dto.AddCartItemCommand;
import com.n11bootcamp.ecommerce.cart.application.dto.UpdateCartItemCommand;
import com.n11bootcamp.ecommerce.cart.application.port.in.*;
import com.n11bootcamp.ecommerce.cart.interfaces.rest.dto.AddCartItemRequest;
import com.n11bootcamp.ecommerce.cart.interfaces.rest.dto.CartResponse;
import com.n11bootcamp.ecommerce.cart.interfaces.rest.dto.UpdateCartItemRequest;
import com.n11bootcamp.ecommerce.cart.interfaces.rest.mapper.CartMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Sepet yönetimi")
public class CartController {

    private final GetCartUseCase getCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final UpdateCartItemUseCase updateCartItemUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final CartMapper cartMapper;

    @GetMapping("/me")
    @Operation(summary = "Sepeti getir")
    public ResponseEntity<Map<String, Object>> getCart(@RequestHeader("X-User-Id") UUID userId) {
        var cart = getCartUseCase.execute(userId);
        return ok(cartMapper.toResponse(cart));
    }

    @PostMapping("/me/items")
    @Operation(summary = "Sepete ürün ekle")
    public ResponseEntity<Map<String, Object>> addItem(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody AddCartItemRequest request) {
        var command = new AddCartItemCommand(userId, request.productId(), request.variantId(), request.quantity());
        var cart = addCartItemUseCase.execute(command);
        return ok(cartMapper.toResponse(cart));
    }

    @PutMapping("/me/items/{variantId}")
    @Operation(summary = "Sepetteki ürün adedini güncelle")
    public ResponseEntity<Map<String, Object>> updateItem(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID variantId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        var command = new UpdateCartItemCommand(userId, variantId, request.quantity());
        var cart = updateCartItemUseCase.execute(command);
        return ok(cartMapper.toResponse(cart));
    }

    @DeleteMapping("/me/items/{variantId}")
    @Operation(summary = "Sepetten ürün sil")
    public ResponseEntity<Map<String, Object>> removeItem(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID variantId) {
        var cart = removeCartItemUseCase.execute(userId, variantId);
        return ok(cartMapper.toResponse(cart));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Sepeti temizle")
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") UUID userId) {
        clearCartUseCase.execute(userId);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String, Object>> ok(CartResponse data) {
        return ResponseEntity.ok(Map.of("data", data, "timestamp", Instant.now()));
    }
}