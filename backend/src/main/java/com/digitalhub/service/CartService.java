package com.digitalhub.service;

import com.digitalhub.dto.CartItemDto;
import com.digitalhub.exception.BadRequestException;
import com.digitalhub.exception.ResourceNotFoundException;
import com.digitalhub.model.CartItem;
import com.digitalhub.model.Product;
import com.digitalhub.model.User;
import com.digitalhub.repository.CartItemRepository;
import com.digitalhub.repository.ProductRepository;
import com.digitalhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CartItemDto> getUserCart(Long userId) {
        return cartItemRepository.findByCustomerIdOrderByAddedAtDesc(userId).stream()
                .map(CartItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CartItemDto addToCart(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (cartItemRepository.findByCustomerIdAndProductId(userId, productId).isPresent()) {
            throw new BadRequestException("This digital item is already in your cart");
        }

        CartItem item = new CartItem(user, product);
        item = cartItemRepository.save(item);
        return CartItemDto.fromEntity(item);
    }

    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        cartItemRepository.deleteByCustomerIdAndProductId(userId, productId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByCustomerId(userId);
    }
}