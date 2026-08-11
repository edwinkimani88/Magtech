-- MagTech Marketplace Database Schema
-- Run this in phpMyAdmin or MySQL CLI

CREATE DATABASE IF NOT EXISTS `magtech_marketplace`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `magtech_marketplace`;

-- ============================================================
-- ITEMS TABLE (synced from Android App)
-- ============================================================
CREATE TABLE IF NOT EXISTS `marketplace_items` (
  `id`                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `remote_item_id`         INT UNSIGNED UNIQUE NOT NULL COMMENT 'ID from Android App',
  `item_name`              VARCHAR(255) NOT NULL,
  `category`               VARCHAR(100) NOT NULL,
  `brand`                  VARCHAR(100) DEFAULT NULL,
  `condition_grade`        VARCHAR(50) DEFAULT 'Good' COMMENT 'Like New, Good, Fair, For Parts',
  `estimated_market_value` DECIMAL(12,2) DEFAULT NULL,
  `marketplace_price`      DECIMAL(12,2) NOT NULL,
  `shop_location`          VARCHAR(50) NOT NULL COMMENT 'Shop 1 or Shop 2',
  `shop_branch_name`       VARCHAR(100) DEFAULT NULL,
  `shop_contact_phone`     VARCHAR(30) DEFAULT NULL,
  `status`                 VARCHAR(50) DEFAULT 'FORFEITED' COMMENT 'FORFEITED, DIRECT_BUY, CONSIGNMENT, REDEEMED, SOLD',
  `is_published`           TINYINT(1) DEFAULT 0,
  `is_available`           TINYINT(1) DEFAULT 1,
  `photo_urls`             JSON DEFAULT NULL COMMENT 'Array of photo URL strings',
  `notes`                  TEXT DEFAULT NULL,
  `views_count`            INT DEFAULT 0,
  `updated_at_timestamp`   BIGINT DEFAULT NULL COMMENT 'Unix ms from Android',
  `created_at`             DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at`             DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  INDEX `idx_category`     (`category`),
  INDEX `idx_shop`         (`shop_location`),
  INDEX `idx_published`    (`is_published`, `is_available`),
  INDEX `idx_price`        (`marketplace_price`),
  INDEX `idx_brand`        (`brand`),
  FULLTEXT INDEX `ft_search` (`item_name`, `brand`, `notes`)
) ENGINE=InnoDB;

-- ============================================================
-- API SYNC LOG (for debugging and audit)
-- ============================================================
CREATE TABLE IF NOT EXISTS `sync_log` (
  `id`         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `action`     VARCHAR(50) NOT NULL,
  `item_id`    INT UNSIGNED DEFAULT NULL,
  `payload`    JSON DEFAULT NULL,
  `status`     VARCHAR(20) DEFAULT 'ok',
  `message`    TEXT DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- DEMO SEED DATA (for local development / before Android sync)
-- ============================================================
INSERT INTO `marketplace_items`
  (remote_item_id, item_name, category, brand, condition_grade, estimated_market_value, marketplace_price, shop_location, shop_branch_name, shop_contact_phone, status, is_published, is_available, photo_urls, notes)
VALUES
  (1001, 'Samsung Galaxy S23 Ultra', 'Phones', 'Samsung', 'Like New', 95000, 85000, 'Shop 1', 'Shop 1 (Westlands)', '+254712345678', 'FORFEITED', 1, 1, '["assets/img/product-phone.png"]', 'Original charger included. Screen protector on. IMEI verified.'),
  (1002, 'HP EliteBook 840 G8 i5', 'Laptops', 'HP', 'Good', 65000, 52000, 'Shop 2', 'Shop 2 (CBD)', '+254798765432', 'DIRECT_BUY', 1, 1, '["assets/img/product-laptop.png"]', 'Fully functional. Battery health 87%. Charger included.'),
  (1003, 'Sony Bravia 55" 4K Smart TV', 'TVs & Audio', 'Sony', 'Like New', 48000, 42000, 'Shop 1', 'Shop 1 (Westlands)', '+254712345678', 'FORFEITED', 1, 1, '["assets/img/product-tv.png"]', 'Remote included. WiFi tested. No scratches on screen.'),
  (1004, 'Samsung Side-by-Side Fridge', 'Fridges & Appliances', 'Samsung', 'Good', 75000, 60000, 'Shop 2', 'Shop 2 (CBD)', '+254798765432', 'CONSIGNMENT', 1, 1, '["assets/img/product-fridge.png"]', 'Frost-free. Water dispenser works. Clean interior.'),
  (1005, 'Sony HT-S20R Home Theatre', 'TVs & Audio', 'Sony', 'Good', 28000, 22000, 'Shop 1', 'Shop 1 (Westlands)', '+254712345678', 'FORFEITED', 1, 1, '["assets/img/product-speakers.png"]', 'All 5 speakers present. Subwoofer works fine. Remote included.'),
  (1006, 'iPhone 14 Pro Max 256GB', 'Phones', 'Apple', 'Like New', 120000, 105000, 'Shop 1', 'Shop 1 (Westlands)', '+254712345678', 'FORFEITED', 1, 1, '["assets/img/product-phone.png"]', 'iCloud unlocked. Face ID working. Battery 91%.'),
  (1007, 'Dell Latitude 5520 i7', 'Laptops', 'Dell', 'Good', 72000, 60000, 'Shop 2', 'Shop 2 (CBD)', '+254798765432', 'DIRECT_BUY', 1, 1, '["assets/img/product-laptop.png"]', '16GB RAM, 512GB SSD. Fast and clean. Charger included.'),
  (1008, 'LG 43" Smart TV WebOS', 'TVs & Audio', 'LG', 'Fair', 28000, 22000, 'Shop 2', 'Shop 2 (CBD)', '+254798765432', 'FORFEITED', 1, 1, '["assets/img/product-tv.png"]', 'Minor frame scratch. Screen perfect. Smart features working.'),
  (1009, 'PlayStation 5 Console', 'Gaming', 'Sony', 'Good', 75000, 62000, 'Shop 1', 'Shop 1 (Westlands)', '+254712345678', 'CONSIGNMENT', 1, 1, '["assets/img/product-speakers.png"]', '2 controllers included. Disc edition. All cables present.'),
  (1010, 'Hisense 310L Double Door Fridge', 'Fridges & Appliances', 'Hisense', 'Good', 38000, 32000, 'Shop 2', 'Shop 2 (CBD)', '+254798765432', 'DIRECT_BUY', 1, 1, '["assets/img/product-fridge.png"]', 'Cooling perfect. Freezer working. Minor dents on side.'),
  (1011, 'Samsung Galaxy Tab S8', 'Phones', 'Samsung', 'Like New', 55000, 48000, 'Shop 1', 'Shop 1 (Westlands)', '+254712345678', 'FORFEITED', 1, 1, '["assets/img/product-phone.png"]', 'S Pen included. 128GB. WiFi + LTE. Screen immaculate.'),
  (1012, 'JBL PartyBox 310 Speaker', 'TVs & Audio', 'JBL', 'Good', 36000, 29000, 'Shop 2', 'Shop 2 (CBD)', '+254798765432', 'FORFEITED', 1, 1, '["assets/img/product-speakers.png"]', 'Full charge, sound tested 100%. Lights working. Handle intact.');
