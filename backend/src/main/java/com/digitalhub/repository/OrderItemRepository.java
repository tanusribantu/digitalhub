package com.digitalhub.repository;

import com.digitalhub.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.seller.id = :sellerId ORDER BY oi.order.createdAt DESC")
    List<OrderItem> findBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.customer.id = :customerId AND oi.product.id = :productId AND oi.order.status = 'COMPLETED'")
    List<OrderItem> findCompletedPurchase(@Param("customerId") Long customerId, @Param("productId") Long productId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.customer.id = :customerId AND oi.order.status = 'COMPLETED' ORDER BY oi.order.createdAt DESC")
    List<OrderItem> findAllCompletedByCustomer(@Param("customerId") Long customerId);
}