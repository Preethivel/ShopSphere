package com.shopsphere.shopsphere.dto;

import lombok.Data;

@Data
public class CartItemRequest {

    private Long variantId;
    // which variant to add — "30 Tablets" or "60 Tablets"

    private Integer quantity;
    // how many to add
}