package com.shopsphere.shopsphere.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ProductVariantResponse {

    private Long id;
    private String variantName;
    private String sku;
    private BigDecimal price;
    private BigDecimal mrp;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private String attributes;
    private Boolean isActive;
}