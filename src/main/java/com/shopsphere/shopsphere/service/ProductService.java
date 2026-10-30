package com.shopsphere.shopsphere.service;

import com.shopsphere.shopsphere.dto.*;
import com.shopsphere.shopsphere.model.*;
import com.shopsphere.shopsphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
// @Transactional = all DB operations in one method run as one transaction
// if anything fails midway, everything rolls back — no partial data saved
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;

    public ProductResponse createProduct(ProductRequest request, String sellerEmail) {

        // Step 1 — find seller by email extracted from JWT token
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        // Step 2 — check seller role
        if (!seller.getRole().equals("SELLER")) {
            throw new RuntimeException("Only sellers can create products");
        }

        // Step 3 — generate slug from product name
        // "Zanskar Joint Support" → "zanskar-joint-support"
        String slug = generateSlug(request.getName());

        // Step 4 — check slug uniqueness
        if (productRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
            // append timestamp to make it unique
        }

        // Step 5 — build product object
        Product product = Product.builder()
                .seller(seller)
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .category(request.getCategory())
                .subcategory(request.getSubcategory())
                .brand(request.getBrand())
                .tags(request.getTags())
                .weight(request.getWeight())
                .dimensions(request.getDimensions())
                .isReturnable(request.getIsReturnable() != null ? request.getIsReturnable() : true)
                .warrantyMonths(request.getWarrantyMonths() != null ? request.getWarrantyMonths() : 0)
                .status("DRAFT")
                .build();

        // Step 6 — save product first to get id
        Product savedProduct = productRepository.save(product);

        // Step 7 — save variants
        if (request.getVariants() != null) {
            List<ProductVariant> variants = request.getVariants().stream()
                    .map(v -> ProductVariant.builder()
                            .product(savedProduct)
                            .variantName(v.getVariantName())
                            .sku(v.getSku())
                            .price(v.getPrice())
                            .mrp(v.getMrp())
                            .stockQuantity(v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                            .lowStockThreshold(v.getLowStockThreshold() != null ? v.getLowStockThreshold() : 10)
                            .attributes(v.getAttributes())
                            .build())
                    .collect(Collectors.toList());
            productVariantRepository.saveAll(variants);
            savedProduct.setVariants(variants);
        }

        // Step 8 — save images
        if (request.getImages() != null) {
            List<ProductImage> images = request.getImages().stream()
                    .map(i -> ProductImage.builder()
                            .product(savedProduct)
                            .imageUrl(i.getImageUrl())
                            .altText(i.getAltText())
                            .displayOrder(i.getDisplayOrder() != null ? i.getDisplayOrder() : 0)
                            .build())
                    .collect(Collectors.toList());
            productImageRepository.saveAll(images);
            savedProduct.setImages(images);
        }

        return mapToResponse(savedProduct);
    }

    public List<ProductResponse> getSellerProducts(String sellerEmail) {
        // get all products for a specific seller
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        return productRepository.findBySellerId(seller.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductBySlug(String slug) {
        // get product by slug for product detail page
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToResponse(product);
    }

    public List<ProductResponse> getProductsByCategory(String category) {
        // get all active products in a category
        return productRepository.findByCategoryAndStatus(category, "ACTIVE")
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private String generateSlug(String name) {
        // "Zanskar Joint Support 30 Tablets" → "zanskar-joint-support-30-tablets"
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                // remove special characters
                .replaceAll("\\s+", "-")
                // replace spaces with hyphens
                .replaceAll("-+", "-")
                // remove duplicate hyphens
                .trim();
    }

    private ProductResponse mapToResponse(Product product) {
        // convert Product entity to ProductResponse DTO
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .category(product.getCategory())
                .subcategory(product.getSubcategory())
                .brand(product.getBrand())
                .tags(product.getTags())
                .weight(product.getWeight())
                .dimensions(product.getDimensions())
                .isReturnable(product.getIsReturnable())
                .warrantyMonths(product.getWarrantyMonths())
                .averageRating(product.getAverageRating())
                .ratingCount(product.getRatingCount())
                .status(product.getStatus())
                .rejectionReason(product.getRejectionReason())
                .variants(product.getVariants().stream()
                        .map(v -> ProductVariantResponse.builder()
                                .id(v.getId())
                                .variantName(v.getVariantName())
                                .sku(v.getSku())
                                .price(v.getPrice())
                                .mrp(v.getMrp())
                                .stockQuantity(v.getStockQuantity())
                                .lowStockThreshold(v.getLowStockThreshold())
                                .attributes(v.getAttributes())
                                .isActive(v.getIsActive())
                                .build())
                        .collect(Collectors.toList()))
                .images(product.getImages().stream()
                        .map(i -> ProductImageResponse.builder()
                                .id(i.getId())
                                .imageUrl(i.getImageUrl())
                                .altText(i.getAltText())
                                .displayOrder(i.getDisplayOrder())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}