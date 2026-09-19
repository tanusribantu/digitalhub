package com.digitalhub.dto;

import com.digitalhub.model.Coupon;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponDto {
    private Long id;
    private String code;
    private Integer discountPercent;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderAmount;
    private LocalDateTime expiryDate;
    private Integer usageLimit;
    private Integer timesUsed;
    private boolean isActive;

    public CouponDto() {}

    public static CouponDto fromEntity(Coupon c) {
        CouponDto dto = new CouponDto();
        dto.setId(c.getId());
        dto.setCode(c.getCode());
        dto.setDiscountPercent(c.getDiscountPercent());
        dto.setMaxDiscountAmount(c.getMaxDiscountAmount());
        dto.setMinOrderAmount(c.getMinOrderAmount());
        dto.setExpiryDate(c.getExpiryDate());
        dto.setUsageLimit(c.getUsageLimit());
        dto.setTimesUsed(c.getTimesUsed());
        dto.setActive(c.isActive());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }
    public BigDecimal getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }
    public BigDecimal getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(BigDecimal minOrderAmount) { this.minOrderAmount = minOrderAmount; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }
    public Integer getTimesUsed() { return timesUsed; }
    public void setTimesUsed(Integer timesUsed) { this.timesUsed = timesUsed; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}