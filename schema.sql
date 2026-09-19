-- ==============================================================================
-- DigitalHub Database Schema (MySQL 8.0+)
-- Complete DDL with Primary Keys, Foreign Keys, Unique Keys, and Indexes
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS `digitalhub_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `digitalhub_db`;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(30) NOT NULL DEFAULT 'ROLE_CUSTOMER',
    `avatar_url` VARCHAR(500) NULL,
    `enabled` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_users_email` (`email`),
    INDEX `idx_users_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Seller Profiles Table
CREATE TABLE IF NOT EXISTS `seller_profiles` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    `store_name` VARCHAR(150) NOT NULL,
    `bio` TEXT NULL,
    `website` VARCHAR(255) NULL,
    `total_sales` INT DEFAULT 0,
    `total_earnings` DECIMAL(12, 2) DEFAULT 0.00,
    `rating` DOUBLE DEFAULT 5.0,
    `is_approved` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_seller_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Categories Table
CREATE TABLE IF NOT EXISTS `categories` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE,
    `slug` VARCHAR(100) NOT NULL UNIQUE,
    `description` VARCHAR(500) NULL,
    `icon` VARCHAR(100) NULL,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX `idx_categories_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Products Table
CREATE TABLE IF NOT EXISTS `products` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `slug` VARCHAR(255) NOT NULL UNIQUE,
    `short_description` VARCHAR(500) NULL,
    `full_description` LONGTEXT NULL,
    `price` DECIMAL(10, 2) NOT NULL,
    `discount_percent` INT DEFAULT 0,
    `category_id` BIGINT NOT NULL,
    `seller_id` BIGINT NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `featured` BOOLEAN NOT NULL DEFAULT FALSE,
    `file_format` VARCHAR(50) NULL,
    `file_size` VARCHAR(50) NULL,
    `version` VARCHAR(30) DEFAULT '1.0',
    `demo_url` VARCHAR(500) NULL,
    `preview_image_url` VARCHAR(500) NULL,
    `download_count` INT DEFAULT 0,
    `average_rating` DOUBLE DEFAULT 0.0,
    `review_count` INT DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_product_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_product_slug` (`slug`),
    INDEX `idx_product_status` (`status`),
    INDEX `idx_product_price` (`price`),
    INDEX `idx_product_rating` (`average_rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Product Files Table
CREATE TABLE IF NOT EXISTS `product_files` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL UNIQUE,
    `original_file_name` VARCHAR(255) NOT NULL,
    `stored_file_name` VARCHAR(255) NOT NULL,
    `file_size_bytes` BIGINT NULL,
    `file_type` VARCHAR(100) NULL,
    `download_token` VARCHAR(100) UNIQUE NULL,
    `uploaded_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_product_file_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Coupons Table
CREATE TABLE IF NOT EXISTS `coupons` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(50) NOT NULL UNIQUE,
    `discount_percent` INT NOT NULL,
    `max_discount_amount` DECIMAL(10, 2) NULL,
    `min_order_amount` DECIMAL(10, 2) NULL,
    `expiry_date` DATETIME NULL,
    `usage_limit` INT NULL,
    `times_used` INT DEFAULT 0,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX `idx_coupon_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Orders Table
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_number` VARCHAR(50) NOT NULL UNIQUE,
    `customer_id` BIGINT NOT NULL,
    `total_amount` DECIMAL(10, 2) NOT NULL,
    `discount_amount` DECIMAL(10, 2) DEFAULT 0.00,
    `final_amount` DECIMAL(10, 2) NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    `payment_method` VARCHAR(50) NOT NULL,
    `coupon_code` VARCHAR(50) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_orders_customer` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT,
    INDEX `idx_orders_customer` (`customer_id`),
    INDEX `idx_orders_number` (`order_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Order Items Table
CREATE TABLE IF NOT EXISTS `order_items` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `price_at_purchase` DECIMAL(10, 2) NOT NULL,
    `download_access_granted` BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT `fk_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT,
    INDEX `idx_items_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Payments Table
CREATE TABLE IF NOT EXISTS `payments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL UNIQUE,
    `transaction_id` VARCHAR(100) NOT NULL UNIQUE,
    `payment_gateway` VARCHAR(50) NOT NULL,
    `amount` DECIMAL(10, 2) NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    `payment_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Reviews Table
CREATE TABLE IF NOT EXISTS `reviews` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL,
    `customer_id` BIGINT NOT NULL,
    `rating` INT NOT NULL,
    `comment` TEXT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_review_customer` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_customer_product_review` (`product_id`, `customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. Wishlists Table
CREATE TABLE IF NOT EXISTS `wishlists` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `added_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_wishlist_customer` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_wishlist_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_wishlist_customer_product` (`customer_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. Cart Items Table
CREATE TABLE IF NOT EXISTS `cart_items` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INT DEFAULT 1,
    `added_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_cart_customer` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_cart_customer_product` (`customer_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. Downloads Audit Table
CREATE TABLE IF NOT EXISTS `downloads` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_item_id` BIGINT NULL,
    `product_id` BIGINT NOT NULL,
    `customer_id` BIGINT NOT NULL,
    `download_timestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `ip_address` VARCHAR(50) NULL,
    `download_token` VARCHAR(100) NULL,
    CONSTRAINT `fk_download_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_download_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_download_customer` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. Notifications Table
CREATE TABLE IF NOT EXISTS `notifications` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `message` TEXT NOT NULL,
    `is_read` BOOLEAN NOT NULL DEFAULT FALSE,
    `type` VARCHAR(50) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15. Reports Table
CREATE TABLE IF NOT EXISTS `reports` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `reporter_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `reason` TEXT NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_report_reporter` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_report_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;