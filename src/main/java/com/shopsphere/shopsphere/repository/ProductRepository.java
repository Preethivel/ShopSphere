package com.shopsphere.shopsphere.repository;

import com.shopsphere.shopsphere.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // find all products by seller
    List<Product> findBySellerId(Long sellerId);

    // find product by slug
    // used for product detail page URL
    Optional<Product> findBySlug(String slug);

    // find all active products by category
    List<Product> findByCategoryAndStatus(String category, String status);

    // check if slug already exists
    boolean existsBySlug(String slug);

    // check if sku already exists across all variants
    boolean existsByVariantsSku(String sku);
}