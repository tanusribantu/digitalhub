package com.digitalhub.dto;

import com.digitalhub.model.CartItem;
import java.math.BigDecimal;

public class CartItemDto {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productSlug;
    private String previewImageUrl;
    private BigDecimal price;
    private Integer discountPercent;
    private BigDecimal effectivePrice;
    private String fileFormat;
    private String categoryName;
    private Integer quantity;

    public CartItemDto() {}

    public static CartItemDto fromEntity(CartItem item) {
        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
            dto.setProductTitle(item.getProduct().getTitle());
            dto.setProductSlug(item.getProduct().getSlug());
            dto.setPreviewImageUrl(item.getProduct().getPreviewImageUrl());
            dto.setPrice(item.getProduct().getPrice());
            dto.setDiscountPercent(item.getProduct().getDiscountPercent());
            dto.setEffectivePrice(item.getProduct().getEffectivePrice());
            dto.setFileFormat(item.getProduct().getFileFormat());
            if (item.getProduct().getCategory() != null) {
                dto.setCategoryName(item.getProduct().getCategory().getName());
            }
        }
        dto.setQuantity(item.getQuantity());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    public String getProductSlug() { return productSlug; }
    public void setProductSlug(String productSlug) { this.productSlug = productSlug; }
    public String getPreviewImageUrl() { return previewImageUrl; }
    public void setPreviewImageUrl(String previewImageUrl) { this.previewImageUrl = previewImageUrl; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }
    public BigDecimal getEffectivePrice() { return effectivePrice; }
    public void setEffectivePrice(BigDecimal effectivePrice) { this.effectivePrice = effectivePrice; }
    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}