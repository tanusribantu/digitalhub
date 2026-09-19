package com.digitalhub.config;

import com.digitalhub.model.*;
import com.digitalhub.repository.*;
import com.digitalhub.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerProfileRepository sellerProfileRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductFileRepository productFileRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        // 1. Create Users
        User admin = new User("Platform Administrator", "admin@digitalhub.com", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN);
        admin.setAvatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150");
        admin = userRepository.save(admin);

        User seller = new User("Alex Turner", "seller@digitalhub.com", passwordEncoder.encode("seller123"), Role.ROLE_SELLER);
        seller.setAvatarUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150");
        seller = userRepository.save(seller);

        SellerProfile profile = new SellerProfile(seller, "Apex Dev & Design Studio",
                "Crafting industry-standard developer tools, SaaS boilerplates, and sleek UI kits.");
        profile.setWebsite("https://apexstudios.design");
        profile.setTotalSales(142);
        profile.setTotalEarnings(new BigDecimal("4890.00"));
        profile.setRating(4.9);
        sellerProfileRepository.save(profile);

        User customer = new User("Sarah Connor", "customer@digitalhub.com", passwordEncoder.encode("customer123"), Role.ROLE_CUSTOMER);
        customer.setAvatarUrl("https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150");
        customer = userRepository.save(customer);

        // 2. Create Categories
        Category catEbooks = categoryRepository.save(new Category("E-books & Guides", "ebooks", "In-depth technical guides, architecture manuals, and cheat sheets.", "fa-solid fa-book-open"));
        Category catCode = categoryRepository.save(new Category("Code & Scripts", "code", "Production-ready boilerplates, scripts, and microservice starters.", "fa-solid fa-code"));
        Category catUI = categoryRepository.save(new Category("UI/UX Design Kits", "ui-ux", "Figma design systems, mobile UI kits, and wireframe libraries.", "fa-solid fa-palette"));
        Category catWeb = categoryRepository.save(new Category("Website Templates", "templates", "Modern responsive templates built with HTML5, Tailwind, and React.", "fa-solid fa-laptop-code"));
        Category catSlides = categoryRepository.save(new Category("Presentation Decks", "presentations", "Investor pitch decks and modern executive slide packs.", "fa-solid fa-chart-pie"));
        Category catGraphics = categoryRepository.save(new Category("Graphics & 3D", "graphics", "3D Blender assets, vector illustrations, and brand identities.", "fa-solid fa-shapes"));
        Category catIcons = categoryRepository.save(new Category("Fonts & Icons", "fonts-icons", "Pixel-perfect icon sets and modern geometric display typefaces.", "fa-solid fa-icons"));
        Category catEdu = categoryRepository.save(new Category("Courses & Study", "education", "Full stack mastery packs, coding tutorials, and exam prep.", "fa-solid fa-graduation-cap"));

        // 3. Create Sample Products
        List<Product> products = new ArrayList<>();

        products.add(createProduct(
                "SaaSify - Ultimate Spring Boot & React Starter Kit",
                "saasify-spring-boot-react-starter",
                "Production-ready multi-tenant SaaS starter boilerplate with JWT auth, Stripe billing, and dashboard.",
                "SaaSify is a battle-tested full stack SaaS starter kit engineered for developers who want to ship products fast. Built on Spring Boot 3 and React 18, it includes out-of-the-box role-based authentication, subscription management, clean domain-driven architecture, and responsive dark-mode dashboard components.",
                new BigDecimal("49.00"), 20, catCode, seller, true, "ZIP (Source Code)", "34.5 MB", "2.1.0",
                "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=600"
        ));

        products.add(createProduct(
                "Fintech UI Pro - 150+ Figma Mobile & Web Screens",
                "fintech-ui-pro-figma-kit",
                "Complete banking, crypto wallet, and investment platform design system with auto-layout.",
                "Fintech UI Pro delivers over 150 meticulously crafted screens for banking apps, crypto trading platforms, and financial analytics. Features dynamic component variants, responsive autolayout, dark & light themes, and comprehensive design tokens.",
                new BigDecimal("35.00"), 15, catUI, seller, true, "FIG (Figma Archive)", "18.2 MB", "3.0",
                "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=600"
        ));

        products.add(createProduct(
                "Mastering Microservices Architecture (E-Book + Code)",
                "mastering-microservices-architecture-ebook",
                "Comprehensive 450-page guide covering Spring Cloud, Kafka, Docker, Kubernetes, and gRPC.",
                "Level up from monolithic thinking to enterprise distributed systems. This comprehensive handbook provides hands-on code examples, event-driven design patterns with Apache Kafka, service mesh configuration, observability with Prometheus & Grafana, and resilience strategies.",
                new BigDecimal("29.00"), 0, catEbooks, seller, true, "PDF + EPUB + ZIP", "12.8 MB", "1.4",
                "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600"
        ));

        products.add(createProduct(
                "NovaStore - Modern Headless E-Commerce Template",
                "novastore-headless-ecommerce-template",
                "Lightning fast storefront template built with Next.js 14, Tailwind CSS, and headless cart.",
                "NovaStore is designed for blazing speed and high conversion. Packed with faceted product search, animated mini-cart, instant checkout flows, SEO-optimized product schema, and modular CMS blocks.",
                new BigDecimal("39.00"), 25, catWeb, seller, false, "ZIP (Next.js Project)", "22.4 MB", "1.2.0",
                "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=600"
        ));

        products.add(createProduct(
                "PitchMaster - VC Ready Startup Pitch Deck",
                "pitchmaster-vc-pitch-deck",
                "Over 80 versatile slide templates proven to raise venture funding across seed to Series A.",
                "Stop wasting weeks crafting investor pitch presentations. PitchMaster features structured financial projections, TAM/SAM sizing models, competitor matrix graphics, traction charts, and executive summary slides in PowerPoint & Keynote format.",
                new BigDecimal("25.00"), 10, catSlides, seller, true, "PPTX + KEY + PDF", "45.0 MB", "2.0",
                "https://images.unsplash.com/photo-1557804506-669a67965ba0?w=600"
        ));

        products.add(createProduct(
                "Cosmic 3D - 80+ Isometric Tech & Space Illustrations",
                "cosmic-3d-isometric-illustrations",
                "High-resolution transparent PNGs and fully editable Blender source files for landing pages.",
                "Bring your tech landing page to life with high-impact 3D isometric objects. Includes servers, rockets, holographic dashboards, AI neural nodes, and cloud computing assets rendered in ultra 4K resolution.",
                new BigDecimal("32.00"), 0, catGraphics, seller, false, "BLEND + 4K PNG", "128.0 MB", "1.0",
                "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600"
        ));

        products.add(createProduct(
                "Lucid Icons - 2,400+ Sharp Stroke & Duotone Icons",
                "lucid-icons-stroke-duotone-pack",
                "Optimized SVG, React Icons, and webfont bundle covering 28 industry categories.",
                "A clean, minimal, and pixel-precise icon set constructed on a 24x24 grid. Includes stroked, filled, and duotone styles with seamless npm and React component integration.",
                new BigDecimal("22.00"), 30, catIcons, seller, true, "SVG + React + Webfont", "14.6 MB", "4.2",
                "https://images.unsplash.com/photo-1626785774573-4b799315345d?w=600"
        ));

        products.add(createProduct(
                "Full-Stack Java & Spring Boot Interview Mastery Pack",
                "fullstack-java-interview-mastery-pack",
                "500+ solved interview questions, real-world coding problems, and system design diagrams.",
                "Ace your next technical interview at FAANG and top tech firms. Detailed explanations on Java Concurrency, JVM internals, Spring Boot microservices, Distributed Caching with Redis, and Database Indexing tricks.",
                new BigDecimal("19.00"), 0, catEdu, seller, true, "PDF + Solved Code ZIP", "8.9 MB", "2026.1",
                "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=600"
        ));

        // 4. Create Coupons
        couponRepository.save(new Coupon("DIGITAL20", 20, new BigDecimal("20.00"), new BigDecimal("50.00")));
        couponRepository.save(new Coupon("WELCOME10", 10, BigDecimal.ZERO, new BigDecimal("25.00")));
        couponRepository.save(new Coupon("MEGA50", 50, new BigDecimal("50.00"), new BigDecimal("100.00")));

        // 5. Create Reviews for First Product
        Product firstProd = products.get(0);
        Review r1 = new Review();
        r1.setProduct(firstProd);
        r1.setCustomer(customer);
        r1.setRating(5);
        r1.setComment("Incredible boilerplate! Saved my team at least 3 weeks of architectural setup. Clean code and great documentation.");
        reviewRepository.save(r1);

        firstProd.setAverageRating(5.0);
        firstProd.setReviewCount(1);
        firstProd.setDownloadCount(18);
        productRepository.save(firstProd);

        // 6. Create Seed Order for Customer
        Order seedOrder = new Order();
        seedOrder.setOrderNumber("DH-INIT-ORDER-1001");
        seedOrder.setCustomer(customer);
        seedOrder.setTotalAmount(firstProd.getPrice());
        seedOrder.setDiscountAmount(new BigDecimal("9.80"));
        seedOrder.setFinalAmount(firstProd.getEffectivePrice());
        seedOrder.setStatus(OrderStatus.COMPLETED);
        seedOrder.setPaymentMethod("CREDIT_CARD");
        seedOrder.setCouponCode("DIGITAL20");
        seedOrder = orderRepository.save(seedOrder);

        OrderItem seedItem = new OrderItem(seedOrder, firstProd, firstProd.getEffectivePrice());
        orderItemRepository.save(seedItem);

        Payment seedPayment = new Payment();
        seedPayment.setOrder(seedOrder);
        seedPayment.setAmount(seedOrder.getFinalAmount());
        seedPayment.setPaymentGateway("SIMULATED_CARD");
        seedPayment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase());
        seedPayment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(seedPayment);
    }

    private Product createProduct(
            String title, String slug, String shortDesc, String fullDesc,
            BigDecimal price, int discount, Category category, User seller,
            boolean featured, String fileFormat, String fileSize, String version, String previewUrl
    ) {
        Product p = new Product();
        p.setTitle(title);
        p.setSlug(slug);
        p.setShortDescription(shortDesc);
        p.setFullDescription(fullDesc);
        p.setPrice(price);
        p.setDiscountPercent(discount);
        p.setCategory(category);
        p.setSeller(seller);
        p.setStatus(ProductStatus.ACTIVE);
        p.setFeatured(featured);
        p.setFileFormat(fileFormat);
        p.setFileSize(fileSize);
        p.setVersion(version);
        p.setPreviewImageUrl(previewUrl);
        p.setDownloadCount((int) (Math.random() * 80 + 20));
        p.setAverageRating(4.5 + (Math.random() * 0.5));
        p.setReviewCount((int) (Math.random() * 15 + 5));
        p = productRepository.save(p);

        // Create mock physical file in uploads
        String mockFileName = slug + "-package.zip";
        fileStorageService.createMockDigitalAsset(
                mockFileName,
                "=====================================================\n"
                + " DIGITALHUB OFFICIAL PRODUCT PACKAGE\n"
                + "=====================================================\n"
                + "Title: " + title + "\n"
                + "Category: " + category.getName() + "\n"
                + "Author / Studio: " + seller.getFullName() + "\n"
                + "Format: " + fileFormat + "\n"
                + "Version: " + version + "\n"
                + "License: DigitalHub Single-End Product Commercial License\n\n"
                + "INCLUDED FILES:\n"
                + "- /src (Full production source files)\n"
                + "- /assets (Icons, styles, fonts, illustrations)\n"
                + "- /documentation (README.md, setup guide)\n\n"
                + "Thank you for supporting creators on DigitalHub!\n"
        );

        ProductFile pf = new ProductFile();
        pf.setProduct(p);
        pf.setOriginalFileName(slug + "-v" + version + ".zip");
        pf.setStoredFileName(mockFileName);
        pf.setFileSizeBytes(1024L * 1024L * 5); // 5 MB
        pf.setFileType("application/zip");
        pf.setDownloadToken(UUID.randomUUID().toString());
        productFileRepository.save(pf);

        return p;
    }
}