package com.digitalhub.dto;

import com.digitalhub.model.OrderItem;
import java.math.BigDecimal;

public class OrderItemDto {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productSlug;
    private String previewImageUrl;
    private String fileFormat;
    private String fileSize;
    private BigDecimal priceAtPurchase;
    private boolean downloadAccessGranted;

    public OrderItemDto() {}

    public static OrderItemDto fromEntity(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
            dto.setProductTitle(item.getProduct().getTitle());
            dto.setProductSlug(item.getProduct().getSlug());
            dto.setPreviewImageUrl(item.getProduct().getPreviewImageUrl());
            dto.setFileFormat(item.getProduct().getFileFormat());
            dto.setFileSize(item.getProduct().getFileSize());
        }
        dto.setPriceAtPurchase(item.getPriceAtPurchase());
        dto.setDownloadAccessGranted(item.isDownloadAccessGranted());
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
    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
    public String getFileSize() { return fileSize; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }
    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(BigDecimal priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
    public boolean isDownloadAccessGranted() { return downloadAccessGranted; }
    public void setDownloadAccessGranted(boolean downloadAccessGranted) { this.downloadAccessGranted = downloadAccessGranted; }
}