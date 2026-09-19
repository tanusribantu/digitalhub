package com.digitalhub.controller;

import com.digitalhub.dto.*;
import com.digitalhub.security.UserPrincipal;
import com.digitalhub.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getStats(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("Seller dashboard stats", sellerService.getSellerStats(principal.getId())));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("Seller products", sellerService.getSellerProducts(principal.getId())));
    }

    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<List<OrderItemDto>>> getSales(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("Seller sales ledger", sellerService.getSellerSales(principal.getId())));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProductRequest req
    ) {
        ProductResponse product = sellerService.createProduct(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.ok("Product created successfully!", product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest req
    ) {
        ProductResponse product = sellerService.updateProduct(principal.getId(), id, req);
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully!", product));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        sellerService.deleteProduct(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Product marked inactive", null));
    }

    @PostMapping("/products/{id}/upload-file")
    public ResponseEntity<ApiResponse<String>> uploadFile(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        sellerService.uploadProductFile(principal.getId(), id, file);
        return ResponseEntity.ok(ApiResponse.ok("File uploaded successfully", file.getOriginalFilename()));
    }
}