package com.digitalhub.controller;

import com.digitalhub.dto.ApiResponse;
import com.digitalhub.dto.ProductResponse;
import com.digitalhub.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Page<ProductResponse> products = productService.getProducts(
                search, category, minPrice, maxPrice, minRating, page, size, sortBy, direction
        );
        return ResponseEntity.ok(ApiResponse.ok("Products retrieved", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Product found", productService.getProductById(id)));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok("Product found", productService.getProductBySlug(slug)));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getFeatured(@RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(ApiResponse.ok("Featured products", productService.getFeaturedProducts(limit)));
    }

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getTrending(@RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(ApiResponse.ok("Trending products", productService.getTrendingProducts(limit)));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getRecent(@RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(ApiResponse.ok("Recent products", productService.getRecentProducts(limit)));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getTopRated(@RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(ApiResponse.ok("Top rated products", productService.getTopRatedProducts(limit)));
    }
}