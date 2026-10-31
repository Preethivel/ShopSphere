package com.shopsphere.shopsphere.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long variantId;
    private String productName;
    private String variantName;
    private String imageUrl;
    // first image of the product
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    // price × quantity
}