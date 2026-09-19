package com.digitalhub.controller;

import com.digitalhub.dto.ApiResponse;
import com.digitalhub.dto.CouponValidationResponse;
import com.digitalhub.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<CouponValidationResponse>> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal amount
    ) {
        CouponValidationResponse resp = couponService.validateCoupon(code, amount);
        return ResponseEntity.ok(ApiResponse.ok("Validation completed", resp));
    }
}