package com.digitalhub.repository;

import com.digitalhub.model.Order;
import com.digitalhub.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    Optional<Order> findByOrderNumber(String orderNumber);
    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.status = 'COMPLETED'")
    BigDecimal sumTotalRevenue();
}