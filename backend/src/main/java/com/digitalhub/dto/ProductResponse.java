package com.digitalhub.dto;

import com.digitalhub.model.Product;
import com.digitalhub.model.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductResponse {
    private Long id;
    private String title;
    private String slug;
    private String shortDescription;
    private String fullDescription;
    private BigDecimal price;
    private Integer discountPercent;
    private BigDecimal effectivePrice;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private Long sellerId;
    private String sellerName;
    private ProductStatus status;
    private boolean featured;
    private String fileFormat;
    private String fileSize;
    private String version;
    private String demoUrl;
    private String previewImageUrl;
    private Integer downloadCount;
    private Double averageRating;
    private Integer reviewCount;
    private LocalDateTime createdAt;

    public ProductResponse() {}

    public static ProductResponse fromEntity(Product p) {
        ProductResponse dto = new ProductResponse();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setSlug(p.getSlug());
        dto.setShortDescription(p.getShortDescription());
        dto.setFullDescription(p.getFullDescription());
        dto.setPrice(p.getPrice());
        dto.setDiscountPercent(p.getDiscountPercent());
        dto.setEffectivePrice(p.getEffectivePrice());
        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getId());
            dto.setCategoryName(p.getCategory().getName());
            dto.setCategorySlug(p.getCategory().getSlug());
        }
        if (p.getSeller() != null) {
            dto.setSellerId(p.getSeller().getId());
            dto.setSellerName(p.getSeller().getFullName());
        }
        dto.setStatus(p.getStatus());
        dto.setFeatured(p.isFeatured());
        dto.setFileFormat(p.getFileFormat());
        dto.setFileSize(p.getFileSize());
        dto.setVersion(p.getVersion());
        dto.setDemoUrl(p.getDemoUrl());
        dto.setPreviewImageUrl(p.getPreviewImageUrl());
        dto.setDownloadCount(p.getDownloadCount());
        dto.setAverageRating(p.getAverageRating());
        dto.setReviewCount(p.getReviewCount());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public String getFullDescription() { return fullDescription; }
    public void setFullDescription(String fullDescription) { this.fullDescription = fullDescription; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }
    public BigDecimal getEffectivePrice() { return effectivePrice; }
    public void setEffectivePrice(BigDecimal effectivePrice) { this.effectivePrice = effectivePrice; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCategorySlug() { return categorySlug; }
    public void setCategorySlug(String categorySlug) { this.categorySlug = categorySlug; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
    public String getFileSize() { return fileSize; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDemoUrl() { return demoUrl; }
    public void setDemoUrl(String demoUrl) { this.demoUrl = demoUrl; }
    public String getPreviewImageUrl() { return previewImageUrl; }
    public void setPreviewImageUrl(String previewImageUrl) { this.previewImageUrl = previewImageUrl; }
    public Integer getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}