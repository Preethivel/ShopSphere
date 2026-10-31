package com.shopsphere.shopsphere.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {

    private Long id;
    private Long variantId;
    private String productName;
    private String variantName;
    private BigDecimal priceAtOrder;
    private Integer quantity;
    private BigDecimal subtotal;
}