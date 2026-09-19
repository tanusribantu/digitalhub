package com.digitalhub.service;

import com.digitalhub.dto.CouponValidationResponse;
import com.digitalhub.dto.OrderRequest;
import com.digitalhub.dto.OrderResponse;
import com.digitalhub.exception.BadRequestException;
import com.digitalhub.exception.ResourceNotFoundException;
import com.digitalhub.model.*;
import com.digitalhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerProfileRepository sellerProfileRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public OrderResponse checkout(Long userId, OrderRequest req) {
        User customer = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        List<Product> productsToBuy = new ArrayList<>();

        if (req.getSingleProductId() != null) {
            Product p = productRepository.findById(req.getSingleProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            productsToBuy.add(p);
        } else if (req.getProductIds() != null && !req.getProductIds().isEmpty()) {
            productsToBuy = productRepository.findAllById(req.getProductIds());
        } else {
            // Pull from cart
            List<CartItem> cartItems = cartItemRepository.findByCustomerIdOrderByAddedAtDesc(userId);
            if (cartItems.isEmpty()) {
                throw new BadRequestException("Your cart is empty");
            }
            productsToBuy = cartItems.stream().map(CartItem::getProduct).collect(Collectors.toList());
        }

        if (productsToBuy.isEmpty()) {
            throw new BadRequestException("No products selected for purchase");
        }

        // Calculate totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Product p : productsToBuy) {
            totalAmount = totalAmount.add(p.getEffectivePrice());
        }

        // Apply coupon if provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        String couponCode = null;
        if (req.getCouponCode() != null && !req.getCouponCode().trim().isEmpty()) {
            CouponValidationResponse val = couponService.validateCoupon(req.getCouponCode(), totalAmount);
            if (val.isValid()) {
                discountAmount = val.getDiscountAmount();
                couponCode = val.getCode();

                couponRepository.findByCodeIgnoreCase(couponCode).ifPresent(c -> {
                    c.setTimesUsed(c.getTimesUsed() + 1);
                    couponRepository.save(c);
                });
            }
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) finalAmount = BigDecimal.ZERO;

        // Generate Order
        String orderNumber = "DH-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"))
                + "-" + (int) (Math.random() * 9000 + 1000);

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setCustomer(customer);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(finalAmount);
        order.setStatus(OrderStatus.COMPLETED);
        order.setPaymentMethod(req.getPaymentMethod() != null ? req.getPaymentMethod() : "SIMULATED_CARD");
        order.setCouponCode(couponCode);
        order = orderRepository.save(order);

        // Create Order Items & Update Seller Earnings
        List<OrderItem> items = new ArrayList<>();
        for (Product p : productsToBuy) {
            OrderItem item = new OrderItem(order, p, p.getEffectivePrice());
            item = orderItemRepository.save(item);
            items.add(item);

            // Update product download count
            p.setDownloadCount(p.getDownloadCount() + 1);
            productRepository.save(p);

            // Update Seller Profile stats
            if (p.getSeller() != null) {
                sellerProfileRepository.findByUserId(p.getSeller().getId()).ifPresent(sellerProfile -> {
                    sellerProfile.setTotalSales(sellerProfile.getTotalSales() + 1);
                    sellerProfile.setTotalEarnings(sellerProfile.getTotalEarnings().add(p.getEffectivePrice()));
                    sellerProfileRepository.save(sellerProfile);
                });

                // Notify Seller
                notificationRepository.save(new Notification(
                        p.getSeller(),
                        "New Product Sale!",
                        "Your product '" + p.getTitle() + "' was just purchased for $" + p.getEffectivePrice(),
                        "SALE"
                ));
            }
        }
        order.setItems(items);

        // Record simulated Payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(finalAmount);
        payment.setPaymentGateway(order.getPaymentMethod());
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 18).toUpperCase());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        // Notify Buyer
        notificationRepository.save(new Notification(
                customer,
                "Order Successful!",
                "Order " + orderNumber + " completed. Digital files are now ready in your Downloads library.",
                "ORDER"
        ));

        // Clear cart if purchase wasn't single item direct buy
        if (req.getSingleProductId() == null) {
            cartItemRepository.deleteByCustomerId(userId);
        } else {
            // If they bought single item, remove it from cart if present
            cartItemRepository.deleteByCustomerIdAndProductId(userId, req.getSingleProductId());
        }

        return OrderResponse.fromEntity(order);
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(userId).stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        if (!order.getCustomer().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized to view this order");
        }
        return OrderResponse.fromEntity(order);
    }
}