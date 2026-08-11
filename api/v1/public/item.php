<?php
require_once __DIR__ . '/../../../config/config.php';
require_once __DIR__ . '/../../../config/database.php';

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: ' . CORS_ORIGIN);
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') { http_response_code(200); exit; }

// Get ID from URL or query param
$id = $_GET['id'] ?? null;
if (!$id) {
    http_response_code(400);
    echo json_encode(['error' => 'Item ID required.']);
    exit;
}

$db = getDB();

// Increment view counter
$db->prepare("UPDATE marketplace_items SET views_count = views_count + 1 WHERE (id = ? OR remote_item_id = ?) AND is_published = 1")
   ->execute([$id, $id]);

// Fetch full item
$stmt = $db->prepare("
    SELECT * FROM marketplace_items
    WHERE (id = ? OR remote_item_id = ?) AND is_published = 1
    LIMIT 1
");
$stmt->execute([$id, $id]);
$item = $stmt->fetch();

if (!$item) {
    http_response_code(404);
    echo json_encode(['error' => 'Item not found or unavailable.']);
    exit;
}

$item['photo_urls']    = json_decode($item['photo_urls'] ?? '[]', true) ?: [];
$item['primary_photo'] = $item['photo_urls'][0] ?? null;

// Get shop details
$shops       = SHOPS;
$shopKey     = $item['shop_location'];
$item['branch_details'] = $shops[$shopKey] ?? null;

// Related items (same category, excluding this item)
$related = $db->prepare("
    SELECT id, remote_item_id, item_name, brand, condition_grade,
           marketplace_price, shop_location, photo_urls
    FROM marketplace_items
    WHERE category = ? AND is_published = 1 AND is_available = 1
      AND id != ?
    ORDER BY updated_at DESC LIMIT 6
");
$related->execute([$item['category'], $item['id']]);
$relatedItems = $related->fetchAll();
foreach ($relatedItems as &$r) {
    $r['photo_urls']    = json_decode($r['photo_urls'] ?? '[]', true) ?: [];
    $r['primary_photo'] = $r['photo_urls'][0] ?? null;
}

echo json_encode([
    'success' => true,
    'item'    => $item,
    'related' => $relatedItems,
]);
