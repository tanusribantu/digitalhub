package com.digitalhub.repository;

import com.digitalhub.model.Category;
import com.digitalhub.model.Product;
import com.digitalhub.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);
    List<Product> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND (:featured IS NULL OR p.featured = :featured)")
    List<Product> findByFeatured(@Param("featured") Boolean featured, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.downloadCount DESC")
    List<Product> findTrending(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.createdAt DESC")
    List<Product> findRecent(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.averageRating DESC")
    List<Product> findTopRated(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' " +
           "AND (:categorySlug IS NULL OR p.category.slug = :categorySlug) " +
           "AND (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(p.seller.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:minRating IS NULL OR p.averageRating >= :minRating)")
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("categorySlug") String categorySlug,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minRating") Double minRating,
            Pageable pageable
    );
}