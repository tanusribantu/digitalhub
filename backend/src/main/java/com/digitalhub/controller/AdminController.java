package com.digitalhub.controller;

import com.digitalhub.dto.*;
import com.digitalhub.model.ProductStatus;
import com.digitalhub.model.Report;
import com.digitalhub.model.Role;
import com.digitalhub.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getPlatformStats() {
        return ResponseEntity.ok(ApiResponse.ok("Platform analytics", adminService.getPlatformStats()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok("Users list", adminService.getAllUsers()));
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<UserDto>> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("User status updated", adminService.toggleUserStatus(id)));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserDto>> updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        return ResponseEntity.ok(ApiResponse.ok("User role updated", adminService.updateUserRole(id, role)));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        return ResponseEntity.ok(ApiResponse.ok("All platform products", adminService.getAllProducts()));
    }

    @PutMapping("/products/{id}/status")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductStatus(
            @PathVariable Long id,
            @RequestParam ProductStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Product status updated", adminService.updateProductStatus(id, status)));
    }

    @PutMapping("/products/{id}/toggle-featured")
    public ResponseEntity<ApiResponse<ProductResponse>> toggleFeatured(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Featured status toggled", adminService.toggleProductFeatured(id)));
    }

    @GetMapping("/coupons")
    public ResponseEntity<ApiResponse<List<CouponDto>>> getCoupons() {
        return ResponseEntity.ok(ApiResponse.ok("Platform coupons", adminService.getAllCoupons()));
    }

    @PostMapping("/coupons")
    public ResponseEntity<ApiResponse<CouponDto>> createCoupon(@Valid @RequestBody CouponDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Coupon created successfully", adminService.createCoupon(dto)));
    }

    @PutMapping("/coupons/{id}/toggle-status")
    public ResponseEntity<ApiResponse<CouponDto>> toggleCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Coupon status updated", adminService.toggleCouponStatus(id)));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<List<Report>>> getReports() {
        return ResponseEntity.ok(ApiResponse.ok("Platform reports", adminService.getAllReports()));
    }
}