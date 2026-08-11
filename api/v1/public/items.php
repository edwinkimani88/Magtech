<?php
require_once __DIR__ . '/../../../config/config.php';
require_once __DIR__ . '/../../../config/database.php';

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: ' . CORS_ORIGIN);
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') { http_response_code(200); exit; }

$db = getDB();

// ── Query params ─────────────────────────────────────────────
$category  = trim($_GET['category'] ?? 'All');
$shop      = trim($_GET['shop_location'] ?? 'All');
$search    = trim($_GET['search'] ?? '');
$sort      = $_GET['sort'] ?? 'newest';
$page      = max(1, (int)($_GET['page'] ?? 1));
$limit     = min(50, max(1, (int)($_GET['limit'] ?? ITEMS_PER_PAGE)));
$offset    = ($page - 1) * $limit;

// ── Build WHERE ──────────────────────────────────────────────
$where  = ["is_published = 1", "is_available = 1"];
$params = [];

if ($category !== 'All' && $category !== '') {
    $where[]       = "category = :category";
    $params[':category'] = $category;
}

if ($shop !== 'All' && $shop !== '') {
    $where[]    = "shop_location = :shop";
    $params[':shop'] = $shop;
}

if ($search !== '') {
    $where[]        = "(item_name LIKE :search OR brand LIKE :search OR notes LIKE :search)";
    $params[':search'] = '%' . $search . '%';
}

$whereSQL = implode(' AND ', $where);

// ── Sort ─────────────────────────────────────────────────────
$orderSQL = match ($sort) {
    'price_asc'  => 'marketplace_price ASC',
    'price_desc' => 'marketplace_price DESC',
    'popular'    => 'views_count DESC',
    default      => 'updated_at DESC',
};

// ── Count ────────────────────────────────────────────────────
$countStmt = $db->prepare("SELECT COUNT(*) FROM marketplace_items WHERE $whereSQL");
$countStmt->execute($params);
$total = (int)$countStmt->fetchColumn();

// ── Fetch ────────────────────────────────────────────────────
$stmt = $db->prepare("
    SELECT id, remote_item_id, item_name, category, brand,
           condition_grade, marketplace_price, estimated_market_value,
           shop_location, shop_branch_name, shop_contact_phone,
           is_available, photo_urls, views_count, updated_at
    FROM marketplace_items
    WHERE $whereSQL
    ORDER BY $orderSQL
    LIMIT :limit OFFSET :offset
");

foreach ($params as $k => $v) {
    $stmt->bindValue($k, $v);
}
$stmt->bindValue(':limit', $limit, PDO::PARAM_INT);
$stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
$stmt->execute();

$items = $stmt->fetchAll();

// ── Decode JSON photo_urls ────────────────────────────────────
foreach ($items as &$item) {
    $item['photo_urls'] = json_decode($item['photo_urls'] ?? '[]', true) ?: [];
    $item['primary_photo'] = $item['photo_urls'][0] ?? null;
}

// ── Response ─────────────────────────────────────────────────
echo json_encode([
    'success'    => true,
    'total'      => $total,
    'page'       => $page,
    'per_page'   => $limit,
    'pages'      => (int)ceil($total / $limit),
    'items'      => $items,
]);
