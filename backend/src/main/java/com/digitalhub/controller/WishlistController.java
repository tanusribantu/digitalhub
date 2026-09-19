package com.digitalhub.controller;

import com.digitalhub.dto.ApiResponse;
import com.digitalhub.dto.ProductResponse;
import com.digitalhub.security.UserPrincipal;
import com.digitalhub.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getWishlist(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("Wishlist items", wishlistService.getUserWishlist(principal.getId())));
    }

    @PostMapping("/toggle/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> toggleWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId
    ) {
        boolean inWishlist = wishlistService.toggleWishlist(principal.getId(), productId);
        String msg = inWishlist ? "Added to wishlist" : "Removed from wishlist";
        return ResponseEntity.ok(ApiResponse.ok(msg, Map.of("inWishlist", inWishlist)));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId
    ) {
        boolean inWishlist = principal != null && wishlistService.isInWishlist(principal.getId(), productId);
        return ResponseEntity.ok(ApiResponse.ok("Status", Map.of("inWishlist", inWishlist)));
    }
}