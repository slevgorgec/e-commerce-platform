package com.n11bootcamp.ecommerce.payment.interfaces.rest.controller;

import com.n11bootcamp.ecommerce.payment.application.port.in.GetPaymentUseCase;
import com.n11bootcamp.ecommerce.payment.application.port.in.ProcessPaymentCallbackUseCase;
import com.n11bootcamp.ecommerce.payment.domain.model.PaymentStatus;
import com.n11bootcamp.ecommerce.payment.interfaces.rest.mapper.PaymentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Tag(name = "Payments", description = "Ödeme sorgulama ve callback endpoint'leri")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final GetPaymentUseCase getPaymentUseCase;
    private final ProcessPaymentCallbackUseCase processPaymentCallbackUseCase;
    private final PaymentMapper paymentMapper;

    @Value("${iyzico.frontend-result-url}")
    private String frontendResultUrl;

    @Operation(summary = "Sipariş referansına göre ödeme sorgula")
    @GetMapping("/{orderReference}")
    public ResponseEntity<Map<String, Object>> getPayment(
            @PathVariable UUID orderReference,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        log.debug("Ödeme sorgulanıyor: orderReference={}, userId={}", orderReference, userId);
        var payment = getPaymentUseCase.getByOrderReference(orderReference, userId);
        return ResponseEntity.ok(Map.of("data", paymentMapper.toResponse(payment), "timestamp", Instant.now()));
    }

    @Operation(summary = "Iyzico checkout form URL'ini döndür")
    @GetMapping("/{orderReference}/checkout-url")
    public ResponseEntity<Map<String, Object>> getCheckoutUrl(
            @PathVariable UUID orderReference,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        log.debug("Checkout URL sorgulanıyor: orderReference={}", orderReference);

        try {
            var payment = getPaymentUseCase.getByOrderReference(orderReference, userId);

            if (payment.checkoutFormUrl() != null) {
                return ResponseEntity.ok(Map.of(
                        "ready", true,
                        "checkoutFormUrl", payment.checkoutFormUrl(),
                        "timestamp", Instant.now()
                ));
            }

            // Ödeme FAILED ise hata döndür
            if (payment.status().name().equals("FAILED")) {
                var reason = payment.iyzicoResponse() != null ? payment.iyzicoResponse() : "Ödeme başlatılamadı";
                log.warn("Ödeme başarısız durumda: orderReference={}, reason={}", orderReference, reason);
                return ResponseEntity.status(422).body(Map.of(
                        "ready", false,
                        "failed", true,
                        "error", reason,
                        "timestamp", Instant.now()
                ));
            }

            return ResponseEntity.accepted().body(Map.of(
                    "ready", false,
                    "failed", false,
                    "timestamp", Instant.now()
            ));

        } catch (com.n11bootcamp.ecommerce.payment.domain.exception.PaymentNotFoundException e) {
            // Henüz payment kaydı oluşturulmamış — saga devam ediyor
            return ResponseEntity.accepted().body(Map.of(
                    "ready", false,
                    "failed", false,
                    "timestamp", Instant.now()
            ));
        }
    }

    @Operation(summary = "Iyzico ödeme callback (tarayıcı yönlendirmesi)", hidden = true)
    @PostMapping("/iyzico-callback")
    public void handleIyzicoCallback(
            @RequestParam String token,
            HttpServletResponse response
    ) throws IOException {
        log.info("Iyzico callback alındı: token={}", token);

        try {
            var payment = processPaymentCallbackUseCase.execute(token);
            var status = payment.status() == PaymentStatus.COMPLETED ? "success" : "failure";
            var redirectUrl = frontendResultUrl + "?status=" + status + "&orderId=" + payment.orderReference();
            log.info("Callback işlendi, frontend'e yönlendiriliyor: {}", redirectUrl);
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            log.error("Callback işleme hatası: token={}, error={}", token, e.getMessage(), e);
            response.sendRedirect(frontendResultUrl + "?status=error");
        }
    }
}
