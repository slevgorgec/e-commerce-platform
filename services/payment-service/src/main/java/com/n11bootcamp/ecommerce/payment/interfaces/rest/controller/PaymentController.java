package com.n11bootcamp.ecommerce.payment.interfaces.rest.controller;

import com.n11bootcamp.ecommerce.payment.application.port.in.GetPaymentUseCase;
import com.n11bootcamp.ecommerce.payment.interfaces.rest.mapper.PaymentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Tag(name = "Payments", description = "Ödeme sorgulama endpoint'leri")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final GetPaymentUseCase getPaymentUseCase;
    private final PaymentMapper paymentMapper;

    @Operation(summary = "Sipariş referansına göre ödeme sorgula",
               description = "Belirtilen orderReference'a ait ödeme kaydını getirir.")
    @GetMapping("/{orderReference}")
    public ResponseEntity<Map<String, Object>> getPayment(
            @PathVariable UUID orderReference,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        log.debug("Ödeme sorgulanıyor: orderReference={}, userId={}", orderReference, userId);
        var payment = getPaymentUseCase.getByOrderReference(orderReference);
        var response = paymentMapper.toResponse(payment);

        return ResponseEntity.ok(Map.of(
                "data", response,
                "timestamp", Instant.now()
        ));
    }
}
