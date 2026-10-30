package com.shopsphere.shopsphere.repository;

import com.shopsphere.shopsphere.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    // find all variants of a product
    List<ProductVariant> findByProductId(Long productId);

    // check if sku exists
    boolean existsBySku(String sku);
}