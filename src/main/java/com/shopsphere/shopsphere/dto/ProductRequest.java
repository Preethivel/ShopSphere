package com.shopsphere.shopsphere.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductRequest {

    private String name;
    // product name — we generate slug from this automatically

    private String description;

    private String category;

    private String subcategory;

    private String brand;

    private String[] tags;

    private String weight;

    private String dimensions;

    private Boolean isReturnable;

    private Integer warrantyMonths;

    private List<ProductVariantRequest> variants;
    // at least one variant required

    private List<ProductImageRequest> images;
    // at least one image required
}