package com.digitalhub.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardStatsDto {
    private long totalUsers;
    private long totalSellers;
    private long totalProducts;
    private long totalOrders;
    private long totalSales;
    private BigDecimal totalRevenue;
    private List<OrderResponse> recentOrders;
    private List<ProductResponse> topProducts;

    public DashboardStatsDto() {}

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalSellers() { return totalSellers; }
    public void setTotalSellers(long totalSellers) { this.totalSellers = totalSellers; }
    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }
    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    public long getTotalSales() { return totalSales; }
    public void setTotalSales(long totalSales) { this.totalSales = totalSales; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public List<OrderResponse> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<OrderResponse> recentOrders) { this.recentOrders = recentOrders; }
    public List<ProductResponse> getTopProducts() { return topProducts; }
    public void setTopProducts(List<ProductResponse> topProducts) { this.topProducts = topProducts; }
}