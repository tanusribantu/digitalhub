package com.digitalhub.service;

import com.digitalhub.dto.ProductResponse;
import com.digitalhub.exception.ResourceNotFoundException;
import com.digitalhub.model.Product;
import com.digitalhub.model.ProductStatus;
import com.digitalhub.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Page<ProductResponse> getProducts(
            String keyword,
            String categorySlug,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Double minRating,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        Sort sort = Sort.by(direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage = productRepository.searchProducts(
                keyword != null && !keyword.isBlank() ? keyword : null,
                categorySlug != null && !categorySlug.isBlank() ? categorySlug : null,
                minPrice,
                maxPrice,
                minRating,
                pageable
        );

        return productPage.map(ProductResponse::fromEntity);
    }

    public ProductResponse getProductById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductResponse.fromEntity(p);
    }

    public ProductResponse getProductBySlug(String slug) {
        Product p = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));
        return ProductResponse.fromEntity(p);
    }

    public List<ProductResponse> getFeaturedProducts(int limit) {
        return productRepository.findByFeatured(true, PageRequest.of(0, limit)).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getTrendingProducts(int limit) {
        return productRepository.findTrending(PageRequest.of(0, limit)).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getRecentProducts(int limit) {
        return productRepository.findRecent(PageRequest.of(0, limit)).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getTopRatedProducts(int limit) {
        return productRepository.findTopRated(PageRequest.of(0, limit)).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }
}