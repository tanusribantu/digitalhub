package com.digitalhub.controller;

import com.digitalhub.dto.ApiResponse;
import com.digitalhub.dto.CartItemDto;
import com.digitalhub.security.UserPrincipal;
import com.digitalhub.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemDto>>> getCart(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("Cart items", cartService.getUserCart(principal.getId())));
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<ApiResponse<CartItemDto>> addToCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId
    ) {
        CartItemDto item = cartService.addToCart(principal.getId(), productId);
        return ResponseEntity.ok(ApiResponse.ok("Added to cart", item));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId
    ) {
        cartService.removeFromCart(principal.getId(), productId);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", null));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        cartService.clearCart(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared", null));
    }
}