package com.digitalhub.dto;

import java.util.List;

public class OrderRequest {
    private String paymentMethod = "CREDIT_CARD";
    private String couponCode;
    private Long singleProductId; // Optional: for immediate "Buy Now" flow
    private List<Long> productIds;

    public OrderRequest() {}

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public Long getSingleProductId() { return singleProductId; }
    public void setSingleProductId(Long singleProductId) { this.singleProductId = singleProductId; }
    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
}