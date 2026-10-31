package com.shopsphere.shopsphere.controller;

import com.shopsphere.shopsphere.dto.OrderRequest;
import com.shopsphere.shopsphere.dto.OrderResponse;
import com.shopsphere.shopsphere.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    // POST /api/orders — place order from cart
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestBody OrderRequest request,
            Principal principal) {
        OrderResponse response = orderService.placeOrder(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    // GET /api/orders — get all orders for buyer
    public ResponseEntity<List<OrderResponse>> getMyOrders(Principal principal) {
        List<OrderResponse> orders = orderService.getBuyerOrders(principal.getName());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    // GET /api/orders/1 — get specific order
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId,
            Principal principal) {
        OrderResponse order = orderService.getOrderById(orderId, principal.getName());
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/cancel")
    // PUT /api/orders/1/cancel — cancel pending order
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            Principal principal) {
        OrderResponse order = orderService.cancelOrder(orderId, principal.getName());
        return ResponseEntity.ok(order);
    }
}