package com.n11bootcamp.ecommerce.order.interfaces.rest.controller;

import com.n11bootcamp.ecommerce.order.application.port.in.CreateOrderUseCase;
import com.n11bootcamp.ecommerce.order.application.port.in.GetOrderUseCase;
import com.n11bootcamp.ecommerce.order.application.port.in.ListOrdersUseCase;
import com.n11bootcamp.ecommerce.order.interfaces.rest.dto.CreateOrderRequest;
import com.n11bootcamp.ecommerce.order.interfaces.rest.dto.OrderResponse;
import com.n11bootcamp.ecommerce.order.interfaces.rest.mapper.OrderMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Tag(name = "Orders", description = "Sipariş oluşturma ve sorgulama endpoint'leri")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;
    private final OrderMapper orderMapper;

    @Operation(summary = "Kullanıcının siparişlerini listele",
               description = "X-User-Id header'ından elde edilen kullanıcının tüm siparişlerini sayfalı döner.")
    @GetMapping
    public ResponseEntity<Map<String, Object>> listOrders(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.debug("Sipariş listeleme isteği: userId={}, page={}, size={}", userId, page, size);
        var result = listOrdersUseCase.execute(userId, page, size);

        var content = result.content().stream()
                .map(orderMapper::toResponse)
                .toList();

        return ResponseEntity.ok(Map.of(
                "data", Map.of(
                        "content", content,
                        "page", result.page(),
                        "size", result.size(),
                        "totalElements", result.totalElements(),
                        "totalPages", result.totalPages(),
                        "last", result.last()
                ),
                "timestamp", Instant.now()
        ));
    }

    @Operation(summary = "Sipariş detayını getir",
               description = "Belirtilen ID'ye sahip siparişi getirir. Kullanıcı yalnızca kendi siparişine erişebilir.")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrder(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        log.debug("Sipariş detay isteği: orderId={}, userId={}", id, userId);
        var order = getOrderUseCase.getByIdAndUserId(id, userId);
        OrderResponse response = orderMapper.toResponse(order);

        return ResponseEntity.ok(Map.of(
                "data", response,
                "timestamp", Instant.now()
        ));
    }

    @Operation(summary = "Yeni sipariş oluştur",
               description = "Sepet içeriğini kullanarak yeni sipariş oluşturur ve Saga akışını başlatır.")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        log.debug("Sipariş oluşturma isteği: userId={}, itemCount={}", userId, request.items().size());
        var command = orderMapper.toCommand(userId, request);
        var order = createOrderUseCase.execute(command);
        OrderResponse response = orderMapper.toResponse(order);

        return ResponseEntity.status(201).body(Map.of(
                "data", response,
                "timestamp", Instant.now()
        ));
    }
}