package com.shopsphere.shopsphere.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String category;
    private String subcategory;
    private String brand;
    private String[] tags;
    private String weight;
    private String dimensions;
    private Boolean isReturnable;
    private Integer warrantyMonths;
    private BigDecimal averageRating;
    private Integer ratingCount;
    private String status;
    private String rejectionReason;
    private List<ProductVariantResponse> variants;
    private List<ProductImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}