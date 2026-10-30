package com.shopsphere.shopsphere.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageResponse {

    private Long id;
    private String imageUrl;
    private String altText;
    private Integer displayOrder;
}