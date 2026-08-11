<?php
// MagTech Application Configuration

// API Security
define('MAGTECH_API_KEY', 'mt_live_sk_your_secret_key_here_change_me');

// Dynamic Application URL detection (supports local XAMPP and Vercel production)
$proto = (isset($_SERVER['HTTP_X_FORWARDED_PROTO']) && $_SERVER['HTTP_X_FORWARDED_PROTO'] === 'https') || (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on') ? 'https' : 'http';
$host  = $_SERVER['HTTP_HOST'] ?? 'localhost';
$path  = (str_contains($host, 'localhost') || str_contains($host, '127.0.0.1')) ? '/Magtech' : '';
define('APP_URL', rtrim("{$proto}://{$host}{$path}", '/'));
define('UPLOAD_URL', APP_URL . '/uploads/items/');
define('UPLOAD_PATH', __DIR__ . '/../uploads/items/');

// Business Information
define('BUSINESS_NAME', 'MagTech Investments');
define('BUSINESS_TAGLINE', 'Good Finds. Good Prices.');

// Shop Branches
define('SHOPS', [
    'Shop 1' => [
        'name'     => 'Shop 1 (Chairman)',
        'location' => 'Chairman Road, Kitengela',
        'phone'    => '+254712345678',
        'whatsapp' => '+254712345678',
        'maps_url' => 'https://maps.google.com/?q=Chairman+Road+Kitengela+Kenya',
        'hours'    => 'Mon–Sat: 8:30am – 6:00pm',
    ],
    'Shop 2' => [
        'name'     => 'Shop 2 (Deliverance Road)',
        'location' => 'Deliverance Road, Kitengela',
        'phone'    => '+254798765432',
        'whatsapp' => '+254798765432',
        'maps_url' => 'https://maps.google.com/?q=Deliverance+Road+Kitengela+Kenya',
        'hours'    => 'Mon–Sat: 8:30am – 6:30pm',
    ],
]);

// Marketplace Categories
define('CATEGORIES', [
    'Phones',
    'Laptops',
    'TVs & Audio',
    'Gaming',
    'Fridges & Appliances',
    'Cookers',
    'Home Appliances',
    'Accessories',
    'Other Electronics',
]);

// Pagination
define('ITEMS_PER_PAGE', 20);

// API Keys & Services
define('OPENROUTER_API_KEY', getenv('OPENROUTER_API_KEY') ?: 'YOUR_OPENROUTER_API_KEY');
define('SUPABASE_PUBLISHABLE_KEY', getenv('SUPABASE_PUBLISHABLE_KEY') ?: 'YOUR_SUPABASE_PUBLISHABLE_KEY');
define('SUPABASE_SECRET_KEY', getenv('SUPABASE_SECRET_KEY') ?: 'YOUR_SUPABASE_SECRET_KEY');

// APK Info (for /app route)
define('APK_VERSION', '1.0.0');
define('APK_RELEASE_DATE', '2026-08-11');
define('APK_DOWNLOAD_URL', 'https://github.com/edwinkimani88/Magtech/releases/download/latest/magtech-admin.apk');

// Allow CORS from everywhere for API
define('CORS_ORIGIN', '*');

