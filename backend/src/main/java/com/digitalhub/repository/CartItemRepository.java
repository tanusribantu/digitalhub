package com.digitalhub.repository;

import com.digitalhub.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCustomerIdOrderByAddedAtDesc(Long customerId);
    Optional<CartItem> findByCustomerIdAndProductId(Long customerId, Long productId);
    void deleteByCustomerIdAndProductId(Long customerId, Long productId);
    void deleteByCustomerId(Long customerId);
}