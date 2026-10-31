package com.shopsphere.shopsphere.service;

import com.shopsphere.shopsphere.dto.*;
import com.shopsphere.shopsphere.model.*;
import com.shopsphere.shopsphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    public OrderResponse placeOrder(String buyerEmail, OrderRequest request) {

        // Step 1 — get buyer
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        // Step 2 — get cart
        CartResponse cart = cartService.getCart(buyerEmail);

        // Step 3 — validate cart is not empty
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Step 4 — validate stock for all items before creating order
        // check everything first — don't create partial orders
        for (CartItemResponse cartItem : cart.getItems()) {
            ProductVariant variant = productVariantRepository.findById(cartItem.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found: " + cartItem.getVariantId()));

            if (variant.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException(
                    "Insufficient stock for " + cartItem.getProductName() +
                    " - " + cartItem.getVariantName() +
                    ". Available: " + variant.getStockQuantity()
                );
            }
        }

        // Step 5 — create order
        Order order = Order.builder()
                .buyer(buyer)
                .totalAmount(cart.getTotalAmount())
                .deliveryName(request.getDeliveryName())
                .deliveryPhone(request.getDeliveryPhone())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryCity(request.getDeliveryCity())
                .deliveryState(request.getDeliveryState())
                .deliveryPincode(request.getDeliveryPincode())
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .status("PENDING")
                .paymentStatus("PENDING")
                .build();

        Order savedOrder = orderRepository.save(order);

        // Step 6 — create order items and reduce stock
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    ProductVariant variant = productVariantRepository.findById(cartItem.getVariantId())
                            .orElseThrow(() -> new RuntimeException("Variant not found"));

                    // reduce stock
                    variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
                    productVariantRepository.save(variant);

                    // create order item
                    return OrderItem.builder()
                            .order(savedOrder)
                            .variant(variant)
                            .productName(cartItem.getProductName())
                            // captured at order time
                            .variantName(cartItem.getVariantName())
                            // captured at order time
                            .priceAtOrder(cartItem.getPrice())
                            // price at time of order — never changes
                            .quantity(cartItem.getQuantity())
                            .subtotal(cartItem.getSubtotal())
                            .build();
                })
                .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);
        savedOrder.setItems(orderItems);

        // Step 7 — clear cart after order placed
        cartService.clearCart(buyerEmail);

        return mapToResponse(savedOrder);
    }

    public List<OrderResponse> getBuyerOrders(String buyerEmail) {
        // get all orders for a buyer
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyer.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId, String buyerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // verify order belongs to this buyer
        if (!order.getBuyer().getEmail().equals(buyerEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        return mapToResponse(order);
    }

    public OrderResponse cancelOrder(Long orderId, String buyerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // verify order belongs to this buyer
        if (!order.getBuyer().getEmail().equals(buyerEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        // only PENDING orders can be cancelled
        if (!order.getStatus().equals("PENDING")) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }

        // restore stock for each item
        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getVariant();
            variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
            productVariantRepository.save(variant);
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);

        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .deliveryName(order.getDeliveryName())
                .deliveryPhone(order.getDeliveryPhone())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryCity(order.getDeliveryCity())
                .deliveryState(order.getDeliveryState())
                .deliveryPincode(order.getDeliveryPincode())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .notes(order.getNotes())
                .items(order.getItems().stream()
                        .map(item -> OrderItemResponse.builder()
                                .id(item.getId())
                                .variantId(item.getVariant().getId())
                                .productName(item.getProductName())
                                .variantName(item.getVariantName())
                                .priceAtOrder(item.getPriceAtOrder())
                                .quantity(item.getQuantity())
                                .subtotal(item.getSubtotal())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}