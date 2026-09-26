<?php
// MagTech Application Configuration

// Load local .env if present
$envFile = __DIR__ . '/../.env';
if (file_exists($envFile)) {
    $lines = file($envFile, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
    foreach ($lines as $line) {
        $line = trim($line);
        if ($line === '' || str_starts_with($line, '#')) continue;
        if (str_contains($line, '=')) {
            [$key, $val] = explode('=', $line, 2);
            $key = trim($key);
            $val = trim($val, " \t\n\r\0\x0B\"'");
            if (getenv($key) === false || getenv($key) === '') {
                putenv("{$key}={$val}");
                $_ENV[$key] = $val;
                $_SERVER[$key] = $val;
            }
        }
    }
}

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
    'Kitchen',
    'Fridges & Appliances',
    'Accessories',
    'Other Electronics',
]);

// Pagination
define('ITEMS_PER_PAGE', 20);

// API Keys & Services
define('OPENROUTER_API_KEY', getenv('OPENROUTER_API_KEY') ?: '');
define('SUPABASE_URL', rtrim(getenv('SUPABASE_URL') ?: '', '/'));
define('SUPABASE_PUBLISHABLE_KEY', getenv('SUPABASE_PUBLISHABLE_KEY') ?: '');
define('SUPABASE_SECRET_KEY', getenv('SUPABASE_SECRET_KEY') ?: '');

// APK Info (for /app route and /download)
define('APK_FILENAME', 'Magtechloanapp.apk');
define('APK_VERSION',      getenv('APK_VERSION')      ?: '1.0.0');
define('APK_RELEASE_DATE', getenv('APK_RELEASE_DATE') ?: '2026-09-26');
// Use direct static path — confirmed working. PHP /download route kept as fallback.
define('APK_DOWNLOAD_URL', APP_URL . '/downloads/' . rawurlencode(APK_FILENAME));

// Allow CORS from everywhere for API
define('CORS_ORIGIN', '*');

