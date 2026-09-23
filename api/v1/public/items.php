<?php
require_once __DIR__ . '/../../../config/config.php';
require_once __DIR__ . '/../../../config/database.php';
require_once __DIR__ . '/../../../includes/supabase.php';

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: ' . CORS_ORIGIN);
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') { http_response_code(200); exit; }

$category  = trim($_GET['category'] ?? 'All');
$shop      = trim($_GET['shop_location'] ?? 'All');
$search    = trim($_GET['search'] ?? '');
$sort      = $_GET['sort'] ?? 'newest';
$page      = max(1, (int)($_GET['page'] ?? 1));
$limit     = min(50, max(1, (int)($_GET['limit'] ?? ITEMS_PER_PAGE)));
$offset    = ($page - 1) * $limit;

$res = supabaseFetchItems([
    'category'      => $category,
    'shop_location' => $shop,
    'search'        => $search,
    'sort'          => $sort,
    'limit'         => $limit,
    'offset'        => $offset,
]);

$items = $res['items'] ?? [];
$total = (int)($res['total'] ?? count($items));

foreach ($items as &$item) {
    $item['primary_photo'] = $item['photo_urls'][0] ?? null;
}

echo json_encode([
    'success'    => true,
    'source'     => $res['source'] ?? 'supabase_live',
    'total'      => $total,
    'page'       => $page,
    'per_page'   => $limit,
    'pages'      => (int)ceil($total / $limit),
    'items'      => $items,
]);
