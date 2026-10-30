package com.shopsphere.shopsphere.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    // many images belong to one product
    private Product product;

    @Column(nullable = false)
    private String imageUrl;
    // S3 URL later — for now just a string

    private String altText;
    // description of image for SEO and accessibility
    // "Zanskar Joint Support 30 Tablets front view"

    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
    // 0 = first image shown, 1 = second, etc.

    @CreationTimestamp
    private LocalDateTime createdAt;
}