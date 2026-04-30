package com.n11bootcamp.ecommerce.order.application.dto;

import java.util.List;

/**
 * Sayfalama sonuçları için generic wrapper.
 */
public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {}