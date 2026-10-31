package com.shopsphere.shopsphere.controller;

import com.shopsphere.shopsphere.dto.CartItemRequest;
import com.shopsphere.shopsphere.dto.CartResponse;
import com.shopsphere.shopsphere.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    // POST /api/cart/add — add item to cart
    public ResponseEntity<CartResponse> addToCart(
            @RequestBody CartItemRequest request,
            Principal principal) {
        CartResponse response = cartService.addToCart(principal.getName(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    // GET /api/cart — get current cart
    public ResponseEntity<CartResponse> getCart(Principal principal) {
        CartResponse response = cartService.getCart(principal.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove/{variantId}")
    // DELETE /api/cart/remove/1 — remove item from cart
    public ResponseEntity<CartResponse> removeFromCart(
            @PathVariable Long variantId,
            Principal principal) {
        CartResponse response = cartService.removeFromCart(principal.getName(), variantId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{variantId}")
    // PUT /api/cart/update/1 — update item quantity
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long variantId,
            @RequestParam Integer quantity,
            Principal principal) {
        CartResponse response = cartService.updateQuantity(principal.getName(), variantId, quantity);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear")
    // DELETE /api/cart/clear — empty the cart
    public ResponseEntity<Void> clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return ResponseEntity.noContent().build();
    }
}