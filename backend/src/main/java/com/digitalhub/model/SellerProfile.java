package com.digitalhub.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "seller_profiles")
public class SellerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String storeName;

    @Column(length = 1000)
    private String bio;

    private String website;
    private Integer totalSales = 0;
    private BigDecimal totalEarnings = BigDecimal.ZERO;
    private Double rating = 5.0;
    private boolean isApproved = true;
    private LocalDateTime createdAt;

    public SellerProfile() {}

    public SellerProfile(User user, String storeName, String bio) {
        this.user = user;
        this.storeName = storeName;
        this.bio = bio;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.totalSales == null) this.totalSales = 0;
        if (this.totalEarnings == null) this.totalEarnings = BigDecimal.ZERO;
        if (this.rating == null) this.rating = 5.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public Integer getTotalSales() { return totalSales; }
    public void setTotalSales(Integer totalSales) { this.totalSales = totalSales; }
    public BigDecimal getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(BigDecimal totalEarnings) { this.totalEarnings = totalEarnings; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}