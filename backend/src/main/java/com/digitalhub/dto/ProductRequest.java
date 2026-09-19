package com.digitalhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProductRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String shortDescription;
    private String fullDescription;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    private Integer discountPercent = 0;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private boolean featured = false;
    private String fileFormat;
    private String fileSize;
    private String version = "1.0";
    private String demoUrl;
    private String previewImageUrl;

    public ProductRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public String getFullDescription() { return fullDescription; }
    public void setFullDescription(String fullDescription) { this.fullDescription = fullDescription; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
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
}