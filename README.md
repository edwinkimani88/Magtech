# Magtech

# MagTech Investments — Web Marketplace & REST API

The official Web Marketplace storefront and synchronization backend for **MagTech Investments** (Kitengela, Kenya — Shop 1 Chairman & Shop 2 Deliverance Road).

## 🚀 Overview

This web marketplace connects seamlessly with the **MagTech Android Admin App**. Physical inventory, buy-ins, and pawn items published from the Android app are automatically synchronized to the public marketplace.

### Features
- **Editorial Deep Teal UI**: Designed following custom editorial principles using modern CSS custom properties and GSAP animations.
- **Branch-Specific Shopping**: Filter products and contact specific branches directly (**Shop 1: Chairman, Kitengela**, **Shop 2: Deliverance Road, Kitengela**).
- **Direct WhatsApp & Phone Actions**: Pre-formatted Swahili/English messages sent directly to the branch stocking the item.
- **REST Synchronization API**: Mobile app integration endpoints for live item state updates, availability toggling, and photo uploads.
- **Multi-Photo Interactive Gallery**: Lightbox image viewing with thumb navigation.
- **Admin App Download**: Dedicated internal page at `/app` for installing the Android Admin APK.

---

## 🛠️ Architecture & Tech Stack

- **Frontend**: PHP 8+, HTML5, Custom Responsive CSS (Mobile-First Design System), Vanilla JS, GSAP 3.
- **Backend API**: PHP REST APIs with JSON payloads and header key authentication (`X-MagTech-Api-Key`).
- **Database**: Dual MySQL & SQLite support (`config/database.php`).
- **Integrations**: OpenRouter AI, Supabase Storage & Database API support.

---

## 📁 Repository Structure

```
Magtech/
├── api/
│   └── v1/
│       ├── marketplace/
│       │   ├── sync.php           # Android app inventory sync endpoint
│       │   └── photos.php         # Android camera photo upload endpoint
│       └── public/
│           ├── items.php          # Public search, filter, pagination API
│           └── item.php           # Single product detail API
├── assets/
│   ├── css/
│   │   ├── main.css               # Core design tokens, layout & reset
│   │   ├── components.css         # Product cards, gallery, shop sidebars
│   │   └── responsive.css         # Breakpoint rules
│   ├── js/
│   │   ├── main.js                # Search overlay, mobile nav, wishlist
│   │   └── animations.js          # GSAP hero & scroll animations
│   └── img/                       # Brand and product assets
├── config/
│   ├── config.php                 # Constants, shop branches, categories, API keys
│   ├── database.php               # PDO database configuration
│   └── schema.sql                 # MySQL / Database schema
├── downloads/
│   └── magtech-admin.apk          # Android Admin App APK
├── includes/
│   ├── functions.php              # Helper formatting & data utilities
│   ├── header.php                 # Shared site header & navigation
│   └── footer.php                 # Shared site footer & branch contacts
├── index.php                      # Main homepage
├── shop.php                       # Catalog listing & search page
├── product.php                    # Single product detail page
├── app.php                        # Admin APK download route (/app)
└── setup.php                      # Database installer script
```

---

## 🔧 Getting Started

1. Clone the repository into your web server directory (e.g. `htdocs/Magtech`).
2. Run database setup by navigating to:
   ```
   http://localhost/Magtech/setup.php?token=magtechsetup2026
   ```
3. Visit the marketplace at:
   ```
   http://localhost/Magtech/
   ```

---

## 📱 Android Sync Configuration

Configure your MagTech Android app API base URL to:
```
POST http://your-domain.com/api/v1/marketplace/sync
Header: X-MagTech-Api-Key: <YOUR_MAGTECH_API_KEY>
```

---

© 2026 MagTech Investments. All rights reserved.
