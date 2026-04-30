package com.n11bootcamp.ecommerce.product.interfaces.rest.controller;

import com.n11bootcamp.ecommerce.product.application.dto.ProductFilterCommand;
import com.n11bootcamp.ecommerce.product.application.port.in.*;
import com.n11bootcamp.ecommerce.product.interfaces.rest.dto.*;
import com.n11bootcamp.ecommerce.product.interfaces.rest.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Products", description = "Ürün ve varyant yönetimi")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final AddVariantUseCase addVariantUseCase;
    private final ProductMapper mapper;

    @Operation(summary = "Ürünleri listele (filtrelenebilir, sayfalı)")
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        var filter = new ProductFilterCommand(categoryId, minPrice, maxPrice, search, page, size, sortBy, sortDir);
        var result = listProductsUseCase.execute(filter);
        var content = result.content().stream().map(mapper::toResponse).toList();
        var pageData = Map.of(
                "content", content,
                "page", result.page(),
                "size", result.size(),
                "totalElements", result.totalElements(),
                "totalPages", result.totalPages(),
                "last", result.last()
        );
        return ResponseEntity.ok(Map.of("data", pageData, "timestamp", Instant.now()));
    }

    @Operation(summary = "Ürün detayı (ID ile)")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable UUID id) {
        var product = getProductUseCase.getById(id);
        return ResponseEntity.ok(Map.of("data", mapper.toResponse(product), "timestamp", Instant.now()));
    }

    @Operation(summary = "Ürün detayı (slug ile)")
    @GetMapping("/slug/{slug}")
    public ResponseEntity<Map<String, Object>> getBySlug(@PathVariable String slug) {
        var product = getProductUseCase.getBySlug(slug);
        return ResponseEntity.ok(Map.of("data", mapper.toResponse(product), "timestamp", Instant.now()));
    }

    @Operation(summary = "Ürün varyantlarını listele")
    @GetMapping("/{id}/variants")
    public ResponseEntity<Map<String, Object>> getVariants(@PathVariable UUID id) {
        List<VariantResponse> variants = getProductUseCase.getVariantsByProductId(id)
                .stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(Map.of("data", variants, "timestamp", Instant.now()));
    }

    @Operation(summary = "Yeni ürün oluştur (ADMIN)")
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateProductRequest request) {
        var product = createProductUseCase.execute(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", mapper.toResponse(product), "timestamp", Instant.now()));
    }

    @Operation(summary = "Ürün güncelle (ADMIN)")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id,
                                                       @Valid @RequestBody UpdateProductRequest request) {
        var product = updateProductUseCase.execute(mapper.toCommand(id, request));
        return ResponseEntity.ok(Map.of("data", mapper.toResponse(product), "timestamp", Instant.now()));
    }

    @Operation(summary = "Ürünü pasife al (ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteProductUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ürüne varyant ekle (ADMIN)")
    @PostMapping("/{id}/variants")
    public ResponseEntity<Map<String, Object>> addVariant(@PathVariable UUID id,
                                                           @Valid @RequestBody AddVariantRequest request) {
        var variant = addVariantUseCase.execute(mapper.toCommand(id, request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", mapper.toResponse(variant), "timestamp", Instant.now()));
    }

    @Operation(summary = "Varyant bilgisi getir (Cart Service için)")
    @GetMapping("/variants/{variantId}")
    public ResponseEntity<Map<String, Object>> getVariantInfo(@PathVariable UUID variantId) {
        var variant = getProductUseCase.getVariantById(variantId);
        var product = getProductUseCase.getById(variant.productId());
        var response = new VariantInfoResponse(
                variant.productId(),
                variant.id(),
                product.name(),
                variant.variantValue(),
                variant.price(),
                product.active()
        );
        return ResponseEntity.ok(Map.of("data", response, "timestamp", Instant.now()));
    }
}
