package com.n11bootcamp.ecommerce.payment.interfaces.rest;

import com.n11bootcamp.ecommerce.payment.domain.exception.DuplicatePaymentException;
import com.n11bootcamp.ecommerce.payment.domain.exception.PaymentAccessDeniedException;
import com.n11bootcamp.ecommerce.payment.domain.exception.PaymentNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentNotFound(PaymentNotFoundException ex,
                                                                      HttpServletRequest req) {
        log.info("Ödeme bulunamadı: {}", ex.getMessage());
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(PaymentAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(PaymentAccessDeniedException ex,
                                                                   HttpServletRequest req) {
        log.info("Yetkisiz ödeme erişimi: {}", ex.getMessage());
        return error(HttpStatus.FORBIDDEN, "FORBIDDEN", "Bu ödeme kaydına erişim yetkiniz yok", req.getRequestURI());
    }

    @ExceptionHandler(DuplicatePaymentException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicatePayment(DuplicatePaymentException ex,
                                                                       HttpServletRequest req) {
        log.info("Duplike ödeme girişimi: {}", ex.getMessage());
        return error(HttpStatus.CONFLICT, "DUPLICATE_PAYMENT", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Beklenmeyen hata: path={}", req.getRequestURI(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Beklenmeyen bir hata oluştu", req.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String errorCode,
                                                       String message, String path) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now(),
                "status", status.value(),
                "error", errorCode,
                "message", message,
                "path", path
        ));
    }
}

