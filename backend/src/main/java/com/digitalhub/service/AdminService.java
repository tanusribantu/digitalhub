package com.digitalhub.service;

import com.digitalhub.dto.CouponDto;
import com.digitalhub.dto.DashboardStatsDto;
import com.digitalhub.dto.OrderResponse;
import com.digitalhub.dto.ProductResponse;
import com.digitalhub.dto.UserDto;
import com.digitalhub.exception.BadRequestException;
import com.digitalhub.exception.ResourceNotFoundException;
import com.digitalhub.model.*;
import com.digitalhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ReportRepository reportRepository;

    public DashboardStatsDto getPlatformStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalSellers(userRepository.countByRole(Role.ROLE_SELLER));
        stats.setTotalProducts(productRepository.count());
        stats.setTotalOrders(orderRepository.count());

        BigDecimal revenue = orderRepository.sumTotalRevenue();
        stats.setTotalRevenue(revenue != null ? revenue : BigDecimal.ZERO);

        List<OrderResponse> recentOrders = orderRepository.findAll(PageRequest.of(0, 5)).stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
        stats.setRecentOrders(recentOrders);

        List<ProductResponse> topProducts = productRepository.findTrending(PageRequest.of(0, 5)).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        stats.setTopProducts(topProducts);

        return stats;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(!user.isEnabled());
        user = userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(newRole);
        user = userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse updateProductStatus(Long productId, ProductStatus status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setStatus(status);
        product = productRepository.save(product);
        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public ProductResponse toggleProductFeatured(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setFeatured(!product.isFeatured());
        product = productRepository.save(product);
        return ProductResponse.fromEntity(product);
    }

    public List<CouponDto> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(CouponDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CouponDto createCoupon(CouponDto dto) {
        if (couponRepository.findByCodeIgnoreCase(dto.getCode()).isPresent()) {
            throw new BadRequestException("Coupon code already exists");
        }
        Coupon c = new Coupon(
                dto.getCode().toUpperCase().trim(),
                dto.getDiscountPercent(),
                dto.getMinOrderAmount(),
                dto.getMaxDiscountAmount()
        );
        c.setUsageLimit(dto.getUsageLimit());
        c = couponRepository.save(c);
        return CouponDto.fromEntity(c);
    }

    @Transactional
    public CouponDto toggleCouponStatus(Long couponId) {
        Coupon c = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        c.setActive(!c.isActive());
        c = couponRepository.save(c);
        return CouponDto.fromEntity(c);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc();
    }
}