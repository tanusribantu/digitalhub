package com.digitalhub.dto;

import java.math.BigDecimal;

public class CouponValidationResponse {
    private boolean valid;
    private String message;
    private String code;
    private Integer discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;

    public CouponValidationResponse() {}

    public CouponValidationResponse(boolean valid, String message, String code, Integer discountPercent, BigDecimal discountAmount, BigDecimal finalAmount) {
        this.valid = valid;
        this.message = message;
        this.code = code;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
    }

    public static CouponValidationResponse invalid(String message) {
        return new CouponValidationResponse(false, message, null, 0, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
}