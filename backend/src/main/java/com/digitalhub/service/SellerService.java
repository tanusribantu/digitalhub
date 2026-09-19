package com.digitalhub.service;

import com.digitalhub.dto.DashboardStatsDto;
import com.digitalhub.dto.OrderItemDto;
import com.digitalhub.dto.ProductRequest;
import com.digitalhub.dto.ProductResponse;
import com.digitalhub.exception.BadRequestException;
import com.digitalhub.exception.ResourceNotFoundException;
import com.digitalhub.model.*;
import com.digitalhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SellerService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerProfileRepository sellerProfileRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductFileRepository productFileRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public DashboardStatsDto getSellerStats(Long sellerId) {
        SellerProfile profile = sellerProfileRepository.findByUserId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));

        List<Product> products = productRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
        List<OrderItem> salesItems = orderItemRepository.findBySellerId(sellerId);

        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalProducts(products.size());
        stats.setTotalSales(profile.getTotalSales() != null ? profile.getTotalSales() : 0);
        stats.setTotalRevenue(profile.getTotalEarnings() != null ? profile.getTotalEarnings() : java.math.BigDecimal.ZERO);
        stats.setTotalOrders(salesItems.size());
        stats.setTopProducts(products.stream().limit(5).map(ProductResponse::fromEntity).collect(Collectors.toList()));
        return stats;
    }

    public List<ProductResponse> getSellerProducts(Long sellerId) {
        return productRepository.findBySellerIdOrderByCreatedAtDesc(sellerId).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<OrderItemDto> getSellerSales(Long sellerId) {
        return orderItemRepository.findBySellerId(sellerId).stream()
                .map(OrderItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse createProduct(Long sellerId, ProductRequest req) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        String baseSlug = toSlug(req.getTitle());
        String slug = baseSlug;
        int counter = 1;
        while (productRepository.findBySlug(slug).isPresent()) {
            slug = baseSlug + "-" + counter++;
        }

        Product product = new Product();
        product.setTitle(req.getTitle());
        product.setSlug(slug);
        product.setShortDescription(req.getShortDescription());
        product.setFullDescription(req.getFullDescription());
        product.setPrice(req.getPrice());
        product.setDiscountPercent(req.getDiscountPercent() != null ? req.getDiscountPercent() : 0);
        product.setCategory(category);
        product.setSeller(seller);
        product.setStatus(ProductStatus.ACTIVE);
        product.setFeatured(req.isFeatured());
        product.setFileFormat(req.getFileFormat());
        product.setFileSize(req.getFileSize());
        product.setVersion(req.getVersion() != null ? req.getVersion() : "1.0");
        product.setDemoUrl(req.getDemoUrl());
        product.setPreviewImageUrl(req.getPreviewImageUrl());

        product = productRepository.save(product);
        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long sellerId, Long productId, ProductRequest req) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new BadRequestException("Unauthorized to update this product");
        }

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setTitle(req.getTitle());
        product.setShortDescription(req.getShortDescription());
        product.setFullDescription(req.getFullDescription());
        product.setPrice(req.getPrice());
        product.setDiscountPercent(req.getDiscountPercent());
        product.setCategory(category);
        product.setFeatured(req.isFeatured());
        product.setFileFormat(req.getFileFormat());
        product.setFileSize(req.getFileSize());
        product.setVersion(req.getVersion());
        product.setDemoUrl(req.getDemoUrl());
        if (req.getPreviewImageUrl() != null && !req.getPreviewImageUrl().isBlank()) {
            product.setPreviewImageUrl(req.getPreviewImageUrl());
        }

        product = productRepository.save(product);
        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public void deleteProduct(Long sellerId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new BadRequestException("Unauthorized to delete this product");
        }

        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    @Transactional
    public void uploadProductFile(Long sellerId, Long productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new BadRequestException("Unauthorized to manage files for this product");
        }

        String storedName = fileStorageService.storeProductFile(file);

        ProductFile pf = productFileRepository.findByProductId(productId).orElse(new ProductFile());
        pf.setProduct(product);
        pf.setOriginalFileName(file.getOriginalFilename());
        pf.setStoredFileName(storedName);
        pf.setFileSizeBytes(file.getSize());
        pf.setFileType(file.getContentType());
        pf.setDownloadToken(UUID.randomUUID().toString());
        productFileRepository.save(pf);

        // Update product file size info
        long sizeInMb = file.getSize() / (1024 * 1024);
        product.setFileSize(sizeInMb > 0 ? sizeInMb + " MB" : (file.getSize() / 1024) + " KB");
        productRepository.save(product);
    }

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    private String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}