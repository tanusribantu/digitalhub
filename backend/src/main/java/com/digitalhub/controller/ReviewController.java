package com.digitalhub.controller;

import com.digitalhub.dto.ApiResponse;
import com.digitalhub.dto.ReviewDto;
import com.digitalhub.dto.ReviewRequest;
import com.digitalhub.security.UserPrincipal;
import com.digitalhub.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.ok("Product reviews", reviewService.getProductReviews(productId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDto>> addReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest req
    ) {
        ReviewDto review = reviewService.addReview(principal.getId(), productId, req);
        return ResponseEntity.ok(ApiResponse.ok("Review submitted successfully!", review));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDto>> updateReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest req
    ) {
        ReviewDto review = reviewService.updateReview(principal.getId(), reviewId, req);
        return ResponseEntity.ok(ApiResponse.ok("Review updated successfully!", review));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId,
            @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(principal.getId(), reviewId);
        return ResponseEntity.ok(ApiResponse.ok("Review deleted successfully", null));
    }
}