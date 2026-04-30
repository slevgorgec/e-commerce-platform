package com.n11bootcamp.ecommerce.cart.interfaces.rest;

import com.n11bootcamp.ecommerce.cart.domain.exception.CartItemNotFoundException;
import com.n11bootcamp.ecommerce.cart.domain.exception.ProductNotAvailableException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCartItemNotFound(CartItemNotFoundException ex,
                                                                       HttpServletRequest req) {
        log.info("Sepet ürünü bulunamadı: {}", ex.getMessage());
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotAvailable(ProductNotAvailableException ex,
                                                                          HttpServletRequest req) {
        log.info("Ürün sepete eklenemez: {}", ex.getMessage());
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "PRODUCT_NOT_AVAILABLE", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
                                                                 HttpServletRequest req) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
                        (existing, replacement) -> existing));
        log.info("Validasyon hatası: {}", errors);
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now(),
                "status", 400,
                "error", "VALIDATION_FAILED",
                "message", errors,
                "path", req.getRequestURI()
        ));
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
