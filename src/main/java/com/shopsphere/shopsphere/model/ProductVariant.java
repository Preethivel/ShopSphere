package com.shopsphere.shopsphere.model;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_variants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    // many variants belong to one product
    private Product product;

    @Column(nullable = false)
    private String variantName;
    // "30 Tablets", "60 Tablets", "90 Tablets"

    @Column(nullable = false, unique = true)
    private String sku;
    // unique code for inventory tracking

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal mrp;
    // discount = ((mrp - price) / mrp) * 100 — calculated, never stored

    @Column(nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer lowStockThreshold = 10;
    // alert seller when stock goes below this

    @Column(columnDefinition = "jsonb")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private String attributes;
    // stored as JSON string
    // {"flavour": "unflavoured", "count": "30"}

    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}