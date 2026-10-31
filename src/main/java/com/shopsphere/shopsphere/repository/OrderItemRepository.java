package com.shopsphere.shopsphere.repository;

import com.shopsphere.shopsphere.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // get all items in an order
    List<OrderItem> findByOrderId(Long orderId);
}