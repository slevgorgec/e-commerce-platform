package com.n11bootcamp.ecommerce.product.interfaces.rest;

import com.n11bootcamp.ecommerce.product.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(ProductNotFoundException ex,
                                                                      HttpServletRequest req) {
        log.info("Ürün bulunamadı: {}", ex.getMessage());
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryNotFound(CategoryNotFoundException ex,
                                                                       HttpServletRequest req) {
        log.info("Kategori bulunamadı: {}", ex.getMessage());
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(VariantNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleVariantNotFound(VariantNotFoundException ex,
                                                                      HttpServletRequest req) {
        log.info("Varyant bulunamadı: {}", ex.getMessage());
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(SlugAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleSlugExists(SlugAlreadyExistsException ex,
                                                                 HttpServletRequest req) {
        log.info("Slug çakışması: {}", ex.getMessage());
        return error(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStock(InsufficientStockException ex,
                                                                        HttpServletRequest req) {
        log.info("Yetersiz stok: {}", ex.getMessage());
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_STOCK", ex.getMessage(), req.getRequestURI());
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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex,
                                                                   HttpServletRequest req) {
        log.info("Yetki hatası: path={}", req.getRequestURI());
        return error(HttpStatus.FORBIDDEN, "FORBIDDEN", "Bu işlem için yetkiniz yok", req.getRequestURI());
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
