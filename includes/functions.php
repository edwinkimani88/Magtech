<?php
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/database.php';

/**
 * Format KSh price
 */
function formatPrice(float $amount): string {
    return 'KSh ' . number_format($amount, 0, '.', ',');
}

/**
 * Get condition CSS class
 */
function conditionClass(string $cond): string {
    return match(strtolower($cond)) {
        'like new' => 'condition--new',
        'good'     => 'condition--good',
        'fair'     => 'condition--fair',
        default    => 'condition--parts',
    };
}

/**
 * Build WhatsApp link for an item
 */
function whatsappLink(array $item): string {
    $phone   = preg_replace('/\D/', '', $item['shop_contact_phone'] ?? '254712345678');
    $price   = formatPrice($item['marketplace_price']);
    $branch  = $item['shop_branch_name'] ?? $item['shop_location'] ?? 'MagTech';
    $msg     = urlencode("Hujambo MagTech! Nimeona {$item['item_name']} ({$price}) kwenye website yenu. Bado iko {$branch}?");
    return "https://wa.me/{$phone}?text={$msg}";
}

/**
 * Get primary photo URL (with APP_URL prepended if relative)
 */
function primaryPhoto(array $item, string $fallback = ''): string {
    $photos = is_array($item['photo_urls'])
        ? $item['photo_urls']
        : (json_decode($item['photo_urls'] ?? '[]', true) ?: []);

    if (empty($fallback)) {
        $fallback = APP_URL . '/assets/img/placeholder.jpg';
    }

    if (empty($photos)) {
        return $fallback;
    }

    $url = $photos[0];
    // Prepend APP_URL only if it's a relative path (not already http:// or //)
    if (!str_starts_with($url, 'http') && !str_starts_with($url, '//')) {
        $url = APP_URL . '/' . ltrim($url, '/');
    }
    return $url;
}

/**
 * Resolve all photo URLs for an item (with APP_URL prefix if relative)
 */
function allPhotos(array $item, string $fallback = ''): array {
    if (empty($fallback)) {
        $fallback = APP_URL . '/assets/img/placeholder.jpg';
    }
    $photos = is_array($item['photo_urls'])
        ? $item['photo_urls']
        : (json_decode($item['photo_urls'] ?? '[]', true) ?: []);

    if (empty($photos)) {
        return [$fallback];
    }

    return array_map(function (string $url): string {
        if (!str_starts_with($url, 'http') && !str_starts_with($url, '//')) {
            return APP_URL . '/' . ltrim($url, '/');
        }
        return $url;
    }, $photos);
}

/**
 * Slugify item name for SEO-friendly URLs.
 * NOTE: The canonical product URL always uses remote_item_id (integer),
 * ensuring product.php can always resolve the correct record.
 */
function itemSlug(array $item): string {
    $slug = strtolower($item['item_name']);
    $slug = preg_replace('/[^a-z0-9\s-]/', '', $slug);
    $slug = preg_replace('/\s+/', '-', trim($slug));
    return $slug . '-' . $item['remote_item_id'];
}

/**
 * Canonical URL for a product (always by remote_item_id).
 */
function productUrl(array $item): string {
    return APP_URL . '/product?id=' . (int)$item['remote_item_id'];
}

/**
 * Get featured items for homepage
 */
function getFeaturedItems(int $limit = 8): array {
    try {
        $db   = getDB();
        $stmt = $db->prepare("
            SELECT * FROM marketplace_items
            WHERE is_published = 1 AND is_available = 1
            ORDER BY updated_at DESC LIMIT ?
        ");
        $stmt->execute([$limit]);
        $items = $stmt->fetchAll();
        foreach ($items as &$item) {
            $item['photo_urls'] = json_decode($item['photo_urls'] ?? '[]', true) ?: [];
        }
        return $items;
    } catch (Exception $e) {
        return [];
    }
}

/**
 * Get items by category
 */
function getItemsByCategory(string $category, int $limit = 6): array {
    try {
        $db   = getDB();
        $stmt = $db->prepare("
            SELECT * FROM marketplace_items
            WHERE category = ? AND is_published = 1 AND is_available = 1
            ORDER BY updated_at DESC LIMIT ?
        ");
        $stmt->execute([$category, $limit]);
        $items = $stmt->fetchAll();
        foreach ($items as &$item) {
            $item['photo_urls'] = json_decode($item['photo_urls'] ?? '[]', true) ?: [];
        }
        return $items;
    } catch (Exception $e) {
        return [];
    }
}

/**
 * Category icon map
 */
function categoryIcon(string $cat): string {
    return match(true) {
        str_contains($cat, 'Phone')    => '📱',
        str_contains($cat, 'Laptop')   => '💻',
        str_contains($cat, 'TV')       => '📺',
        str_contains($cat, 'Audio')    => '🔊',
        str_contains($cat, 'Gaming')   => '🎮',
        str_contains($cat, 'Kitchen')  => '🍳',
        str_contains($cat, 'Fridge')   => '❄️',
        str_contains($cat, 'Cooker')   => '🍳',
        str_contains($cat, 'Appliance')=> '🏠',
        str_contains($cat, 'Accessor') => '🎧',
        default                         => '⚡',
    };
}
