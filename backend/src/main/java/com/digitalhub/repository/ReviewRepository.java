package com.digitalhub.repository;

import com.digitalhub.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
    Optional<Review> findByProductIdAndCustomerId(Long productId, Long customerId);
    long countByProductId(Long productId);
}