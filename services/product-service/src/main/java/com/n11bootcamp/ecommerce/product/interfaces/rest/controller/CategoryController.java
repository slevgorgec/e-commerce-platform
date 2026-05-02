package com.n11bootcamp.ecommerce.product.interfaces.rest.controller;

import com.n11bootcamp.ecommerce.product.application.port.in.*;
import com.n11bootcamp.ecommerce.product.interfaces.rest.dto.*;
import com.n11bootcamp.ecommerce.product.interfaces.rest.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Categories", description = "Kategori yönetimi")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final ProductMapper mapper;

    @Operation(summary = "Tüm kategorileri listele")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<CategoryResponse> categories = getCategoryUseCase.getAll()
                .stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(Map.of("data", categories, "timestamp", Instant.now()));
    }

    @Operation(summary = "Kategori detayı")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable UUID id) {
        var category = getCategoryUseCase.getById(id);
        return ResponseEntity.ok(Map.of("data", mapper.toResponse(category), "timestamp", Instant.now()));
    }

    @Operation(summary = "Yeni kategori oluştur (ADMIN)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateCategoryRequest request) {
        var category = createCategoryUseCase.execute(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", mapper.toResponse(category), "timestamp", Instant.now()));
    }

    @Operation(summary = "Kategori güncelle (ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id,
                                                       @Valid @RequestBody UpdateCategoryRequest request) {
        var category = updateCategoryUseCase.execute(mapper.toCommand(id, request));
        return ResponseEntity.ok(Map.of("data", mapper.toResponse(category), "timestamp", Instant.now()));
    }

    @Operation(summary = "Kategori sil (ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteCategoryUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
