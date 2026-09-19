package com.digitalhub.dto;

import com.digitalhub.model.Order;
import com.digitalhub.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponse {
    private Long id;
    private String orderNumber;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private OrderStatus status;
    private String paymentMethod;
    private String couponCode;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;

    public OrderResponse() {}

    public static OrderResponse fromEntity(Order o) {
        OrderResponse dto = new OrderResponse();
        dto.setId(o.getId());
        dto.setOrderNumber(o.getOrderNumber());
        dto.setTotalAmount(o.getTotalAmount());
        dto.setDiscountAmount(o.getDiscountAmount());
        dto.setFinalAmount(o.getFinalAmount());
        dto.setStatus(o.getStatus());
        dto.setPaymentMethod(o.getPaymentMethod());
        dto.setCouponCode(o.getCouponCode());
        dto.setCreatedAt(o.getCreatedAt());
        if (o.getItems() != null) {
            dto.setItems(o.getItems().stream().map(OrderItemDto::fromEntity).collect(Collectors.toList()));
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}