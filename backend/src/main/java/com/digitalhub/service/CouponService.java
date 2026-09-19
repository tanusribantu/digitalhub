package com.digitalhub.service;

import com.digitalhub.dto.CouponValidationResponse;
import com.digitalhub.model.Coupon;
import com.digitalhub.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public CouponValidationResponse validateCoupon(String code, BigDecimal orderAmount) {
        if (code == null || code.trim().isEmpty()) {
            return CouponValidationResponse.invalid("Coupon code cannot be empty");
        }

        Optional<Coupon> couponOpt = couponRepository.findByCodeIgnoreCaseAndIsActiveTrue(code.trim());
        if (couponOpt.isEmpty()) {
            return CouponValidationResponse.invalid("Invalid or expired coupon code");
        }

        Coupon coupon = couponOpt.get();

        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            return CouponValidationResponse.invalid("Coupon has expired");
        }

        if (coupon.getUsageLimit() != null && coupon.getTimesUsed() >= coupon.getUsageLimit()) {
            return CouponValidationResponse.invalid("Coupon usage limit reached");
        }

        if (coupon.getMinOrderAmount() != null && orderAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            return CouponValidationResponse.invalid("Minimum order amount of $" + coupon.getMinOrderAmount() + " required");
        }

        BigDecimal discountAmount = orderAmount
                .multiply(BigDecimal.valueOf(coupon.getDiscountPercent()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        if (coupon.getMaxDiscountAmount() != null && discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
            discountAmount = coupon.getMaxDiscountAmount();
        }

        BigDecimal finalAmount = orderAmount.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        return new CouponValidationResponse(
                true,
                "Coupon applied successfully! You saved $" + discountAmount,
                coupon.getCode(),
                coupon.getDiscountPercent(),
                discountAmount,
                finalAmount
        );
    }
}