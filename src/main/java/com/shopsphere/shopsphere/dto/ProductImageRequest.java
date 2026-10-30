package com.shopsphere.shopsphere.dto;

import lombok.Data;

@Data
public class ProductImageRequest {

    private String imageUrl;

    private String altText;

    private Integer displayOrder;
}