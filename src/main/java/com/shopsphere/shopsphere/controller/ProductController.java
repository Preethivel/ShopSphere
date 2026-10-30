package com.shopsphere.shopsphere.controller;

import com.shopsphere.shopsphere.dto.ProductRequest;
import com.shopsphere.shopsphere.dto.ProductResponse;
import com.shopsphere.shopsphere.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    // POST /api/products — seller creates a product
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody ProductRequest request,
            Principal principal) {
            // Principal = currently logged in user
            // Spring extracts this from JWT token automatically
        ProductResponse response = productService.createProduct(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-products")
    // GET /api/products/my-products — seller sees their own products
    public ResponseEntity<List<ProductResponse>> getMyProducts(Principal principal) {
        List<ProductResponse> products = productService.getSellerProducts(principal.getName());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{slug}")
    // GET /api/products/zanskar-joint-support — product detail page
    public ResponseEntity<ProductResponse> getProductBySlug(@PathVariable String slug) {
        ProductResponse product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/category/{category}")
    // GET /api/products/category/health — all active products in a category
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable String category) {
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
}