package com.shopsphere.shopsphere.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductVariantRequest {

    private String variantName;
    // "30 Tablets", "60 Tablets"

    private String sku;

    private BigDecimal price;

    private BigDecimal mrp;

    private Integer stockQuantity;

    private Integer lowStockThreshold;

    private String attributes;
    // JSON string — {"flavour": "unflavoured", "count": "30"}
}