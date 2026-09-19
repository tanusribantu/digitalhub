package com.digitalhub.service;

import com.digitalhub.dto.ReviewDto;
import com.digitalhub.dto.ReviewRequest;
import com.digitalhub.exception.BadRequestException;
import com.digitalhub.exception.ResourceNotFoundException;
import com.digitalhub.model.Product;
import com.digitalhub.model.Review;
import com.digitalhub.model.User;
import com.digitalhub.repository.OrderItemRepository;
import com.digitalhub.repository.ProductRepository;
import com.digitalhub.repository.ReviewRepository;
import com.digitalhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<ReviewDto> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDto addReview(Long customerId, Long productId, ReviewRequest req) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Verify purchase
        boolean hasPurchased = !orderItemRepository.findCompletedPurchase(customerId, productId).isEmpty();
        if (!hasPurchased) {
            throw new BadRequestException("Only verified buyers who completed a purchase can review this product.");
        }

        // Check if user already reviewed
        reviewRepository.findByProductIdAndCustomerId(productId, customerId).ifPresent(r -> {
            throw new BadRequestException("You have already reviewed this product. You may edit your existing review.");
        });

        Review review = new Review();
        review.setProduct(product);
        review.setCustomer(customer);
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review = reviewRepository.save(review);

        updateProductRating(product);

        return ReviewDto.fromEntity(review);
    }

    @Transactional
    public ReviewDto updateReview(Long customerId, Long reviewId, ReviewRequest req) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getCustomer().getId().equals(customerId)) {
            throw new BadRequestException("Unauthorized to edit this review");
        }

        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review = reviewRepository.save(review);

        updateProductRating(review.getProduct());

        return ReviewDto.fromEntity(review);
    }

    @Transactional
    public void deleteReview(Long customerId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getCustomer().getId().equals(customerId)) {
            throw new BadRequestException("Unauthorized to delete this review");
        }

        Product product = review.getProduct();
        reviewRepository.delete(review);
        updateProductRating(product);
    }

    private void updateProductRating(Product product) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(product.getId());
        if (reviews.isEmpty()) {
            product.setAverageRating(0.0);
            product.setReviewCount(0);
        } else {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            product.setAverageRating(Math.round(avg * 10.0) / 10.0);
            product.setReviewCount(reviews.size());
        }
        productRepository.save(product);
    }
}