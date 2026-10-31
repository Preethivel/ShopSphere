package com.shopsphere.shopsphere.repository;

import com.shopsphere.shopsphere.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // get all orders for a buyer
    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    // get orders by status — for admin
    List<Order> findByStatus(String status);
}