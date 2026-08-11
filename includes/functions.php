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
    $name    = urlencode($item['item_name']);
    $price   = formatPrice($item['marketplace_price']);
    $branch  = urlencode($item['shop_branch_name'] ?? $item['shop_location']);
    $msg     = urlencode("Hujambo MagTech! Nimeona {$item['item_name']} ({$price}) kwenye website yenu. Bado iko {$item['shop_branch_name']}?");
    return "https://wa.me/{$phone}?text={$msg}";
}

/**
 * Get primary photo URL (with fallback)
 */
function primaryPhoto(array $item, string $fallback = 'assets/img/placeholder.jpg'): string {
    $photos = is_array($item['photo_urls'])
        ? $item['photo_urls']
        : (json_decode($item['photo_urls'] ?? '[]', true) ?: []);
    return !empty($photos) ? $photos[0] : $fallback;
}

/**
 * Slugify item name for SEO-friendly URLs
 */
function itemSlug(array $item): string {
    $slug = strtolower($item['item_name']);
    $slug = preg_replace('/[^a-z0-9\s-]/', '', $slug);
    $slug = preg_replace('/\s+/', '-', trim($slug));
    return $slug . '-' . $item['remote_item_id'];
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
        str_contains($cat, 'Gaming')   => '🎮',
        str_contains($cat, 'Fridge')   => '❄️',
        str_contains($cat, 'Cooker')   => '🍳',
        str_contains($cat, 'Appliance')=> '🏠',
        str_contains($cat, 'Audio')    => '🔊',
        default                         => '⚡',
    };
}
