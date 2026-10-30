package com.shopsphere.shopsphere.repository;

import com.shopsphere.shopsphere.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    // find all images of a product ordered by display order
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);
}