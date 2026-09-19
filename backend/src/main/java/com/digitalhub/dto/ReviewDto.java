package com.digitalhub.dto;

import com.digitalhub.model.Review;
import java.time.LocalDateTime;

public class ReviewDto {
    private Long id;
    private Long productId;
    private Long customerId;
    private String customerName;
    private String customerAvatar;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewDto() {}

    public static ReviewDto fromEntity(Review r) {
        ReviewDto dto = new ReviewDto();
        dto.setId(r.getId());
        if (r.getProduct() != null) dto.setProductId(r.getProduct().getId());
        if (r.getCustomer() != null) {
            dto.setCustomerId(r.getCustomer().getId());
            dto.setCustomerName(r.getCustomer().getFullName());
            dto.setCustomerAvatar(r.getCustomer().getAvatarUrl());
        }
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerAvatar() { return customerAvatar; }
    public void setCustomerAvatar(String customerAvatar) { this.customerAvatar = customerAvatar; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}