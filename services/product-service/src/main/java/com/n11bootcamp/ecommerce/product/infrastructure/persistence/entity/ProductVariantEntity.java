package com.n11bootcamp.ecommerce.product.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
public class ProductVariantEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "product_id", nullable = false, columnDefinition = "uuid")
    private UUID productId;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(name = "variant_value", nullable = false, length = 100)
    private String variantValue;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @Column(name = "reserved_stock", nullable = false)
    private int reservedStock;
}
