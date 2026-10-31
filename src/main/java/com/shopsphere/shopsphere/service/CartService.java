package com.shopsphere.shopsphere.service;

import com.shopsphere.shopsphere.dto.CartItemRequest;
import com.shopsphere.shopsphere.dto.CartItemResponse;
import com.shopsphere.shopsphere.dto.CartResponse;
import com.shopsphere.shopsphere.model.ProductVariant;
import com.shopsphere.shopsphere.repository.ProductVariantRepository;
import com.shopsphere.shopsphere.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;

    private static final String CART_PREFIX = "cart:";
    // cart key format: "cart:user@email.com"

    private static final long CART_EXPIRY_DAYS = 7;
    // cart expires after 7 days of inactivity

    private String getCartKey(String email) {
        return CART_PREFIX + email;
        // "cart:preethi@shopsphere.com"
    }

    public CartResponse addToCart(String email, CartItemRequest request) {

        // Step 1 — find the variant
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        // Step 2 — check stock
        if (variant.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + variant.getStockQuantity());
        }

        // Step 3 — get existing cart from Redis
        String cartKey = getCartKey(email);
        List<CartItemResponse> items = getCartItems(cartKey);

        // Step 4 — check if variant already in cart
        boolean found = false;
        for (CartItemResponse item : items) {
            if (item.getVariantId().equals(request.getVariantId())) {
                // variant exists — update quantity
                int newQuantity = item.getQuantity() + request.getQuantity();
                if (newQuantity > variant.getStockQuantity()) {
                    throw new RuntimeException("Insufficient stock. Available: " + variant.getStockQuantity());
                }
                item.setQuantity(newQuantity);
                item.setSubtotal(variant.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
                found = true;
                break;
            }
        }

        if (!found) {
            // Step 5 — add new item to cart
            String imageUrl = productImageRepository
                    .findByProductIdOrderByDisplayOrderAsc(variant.getProduct().getId())
                    .stream()
                    .findFirst()
                    .map(img -> img.getImageUrl())
                    .orElse(null);
            // get first image of product

            CartItemResponse newItem = CartItemResponse.builder()
                    .variantId(variant.getId())
                    .productName(variant.getProduct().getName())
                    .variantName(variant.getVariantName())
                    .imageUrl(imageUrl)
                    .price(variant.getPrice())
                    .quantity(request.getQuantity())
                    .subtotal(variant.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                    .build();

            items.add(newItem);
        }

        // Step 6 — save updated cart back to Redis
        redisTemplate.opsForValue().set(cartKey, items, CART_EXPIRY_DAYS, TimeUnit.DAYS);
        // expires in 7 days automatically

        return buildCartResponse(items);
    }

    public CartResponse getCart(String email) {
        String cartKey = getCartKey(email);
        List<CartItemResponse> items = getCartItems(cartKey);
        return buildCartResponse(items);
    }

    public CartResponse removeFromCart(String email, Long variantId) {
        String cartKey = getCartKey(email);
        List<CartItemResponse> items = getCartItems(cartKey);

        items.removeIf(item -> item.getVariantId().equals(variantId));
        // remove item with matching variantId

        redisTemplate.opsForValue().set(cartKey, items, CART_EXPIRY_DAYS, TimeUnit.DAYS);
        return buildCartResponse(items);
    }

    public CartResponse updateQuantity(String email, Long variantId, Integer quantity) {
        String cartKey = getCartKey(email);
        List<CartItemResponse> items = getCartItems(cartKey);

        for (CartItemResponse item : items) {
            if (item.getVariantId().equals(variantId)) {
                ProductVariant variant = productVariantRepository.findById(variantId)
                        .orElseThrow(() -> new RuntimeException("Variant not found"));

                if (quantity > variant.getStockQuantity()) {
                    throw new RuntimeException("Insufficient stock. Available: " + variant.getStockQuantity());
                }

                if (quantity <= 0) {
                    // quantity 0 or less = remove item
                    items.remove(item);
                } else {
                    item.setQuantity(quantity);
                    item.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(quantity)));
                }
                break;
            }
        }

        redisTemplate.opsForValue().set(cartKey, items, CART_EXPIRY_DAYS, TimeUnit.DAYS);
        return buildCartResponse(items);
    }

    public void clearCart(String email) {
        // called after order is placed
        redisTemplate.delete(getCartKey(email));
    }

    @SuppressWarnings("unchecked")
    private List<CartItemResponse> getCartItems(String cartKey) {
        Object cartData = redisTemplate.opsForValue().get(cartKey);
        if (cartData == null) {
            return new ArrayList<>();
            // empty cart
        }
        return (List<CartItemResponse>) cartData;
    }

    private CartResponse buildCartResponse(List<CartItemResponse> items) {
        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // sum all subtotals

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();
        // sum all quantities

        return CartResponse.builder()
                .items(items)
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .build();
    }
}