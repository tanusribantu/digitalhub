package com.digitalhub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "downloads")
public class Download {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    private LocalDateTime downloadTimestamp;
    private String ipAddress;
    private String downloadToken;

    public Download() {}

    public Download(OrderItem orderItem, Product product, User customer, String ipAddress) {
        this.orderItem = orderItem;
        this.product = product;
        this.customer = customer;
        this.ipAddress = ipAddress;
    }

    @PrePersist
    protected void onCreate() {
        this.downloadTimestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public OrderItem getOrderItem() { return orderItem; }
    public void setOrderItem(OrderItem orderItem) { this.orderItem = orderItem; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }
    public LocalDateTime getDownloadTimestamp() { return downloadTimestamp; }
    public void setDownloadTimestamp(LocalDateTime downloadTimestamp) { this.downloadTimestamp = downloadTimestamp; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getDownloadToken() { return downloadToken; }
    public void setDownloadToken(String downloadToken) { this.downloadToken = downloadToken; }
}