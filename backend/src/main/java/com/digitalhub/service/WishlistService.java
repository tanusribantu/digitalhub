package com.digitalhub.service;

import com.digitalhub.dto.ProductResponse;
import com.digitalhub.exception.ResourceNotFoundException;
import com.digitalhub.model.Product;
import com.digitalhub.model.User;
import com.digitalhub.model.Wishlist;
import com.digitalhub.repository.ProductRepository;
import com.digitalhub.repository.UserRepository;
import com.digitalhub.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ProductResponse> getUserWishlist(Long userId) {
        return wishlistRepository.findByCustomerIdOrderByAddedAtDesc(userId).stream()
                .map(w -> ProductResponse.fromEntity(w.getProduct()))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean toggleWishlist(Long userId, Long productId) {
        if (wishlistRepository.existsByCustomerIdAndProductId(userId, productId)) {
            wishlistRepository.deleteByCustomerIdAndProductId(userId, productId);
            return false; // removed
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            wishlistRepository.save(new Wishlist(user, product));
            return true; // added
        }
    }

    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepository.existsByCustomerIdAndProductId(userId, productId);
    }
}