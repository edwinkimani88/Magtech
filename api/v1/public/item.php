<?php
require_once __DIR__ . '/../../../config/config.php';
require_once __DIR__ . '/../../../config/database.php';
require_once __DIR__ . '/../../../includes/supabase.php';

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

$item = supabaseGetItemById($id);

if (!$item) {
    http_response_code(404);
    echo json_encode(['error' => 'Item not found or unavailable.']);
    exit;
}

$item['primary_photo'] = $item['photo_urls'][0] ?? null;

// Get shop details
$shops = SHOPS;
$shopKey = $item['shop_location'];
$item['branch_details'] = $shops[$shopKey] ?? null;

// Related items (same category, excluding this item)
$relRes = supabaseFetchItems([
    'category' => $item['category'],
    'limit'    => 5,
]);
$relatedItems = array_values(array_filter($relRes['items'] ?? [], function($r) use ($item) {
    return ($r['id'] ?? 0) != ($item['id'] ?? 0);
}));
$relatedItems = array_slice($relatedItems, 0, 4);

foreach ($relatedItems as &$r) {
    $r['primary_photo'] = $r['photo_urls'][0] ?? null;
}

echo json_encode([
    'success' => true,
    'item'    => $item,
    'related' => $relatedItems,
]);
