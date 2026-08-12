<?php
require_once __DIR__ . '/../../config/config.php';
require_once __DIR__ . '/../../config/database.php';

// ── CORS & JSON headers ──────────────────────────────────────
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: ' . CORS_ORIGIN);
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, X-MagTech-Api-Key');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') { http_response_code(200); exit; }

// ── Auth ─────────────────────────────────────────────────────
$apiKey = $_SERVER['HTTP_X_MAGTECH_API_KEY'] ?? '';
if ($apiKey !== MAGTECH_API_KEY) {
    http_response_code(401);
    echo json_encode(['error' => 'Unauthorised. Invalid API key.']);
    exit;
}

// ── Only allow POST ──────────────────────────────────────────
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['error' => 'Method not allowed.']);
    exit;
}

// ── Parse JSON body ──────────────────────────────────────────
$raw  = file_get_contents('php://input');
$data = json_decode($raw, true);

if (!$data || !isset($data['remote_item_id'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Invalid payload. remote_item_id required.']);
    exit;
}

$db = getDB();

// ── Determine availability ───────────────────────────────────
$isPublished  = (bool)($data['is_published'] ?? false);
$status       = strtoupper($data['status'] ?? 'UNKNOWN');
$isAvailable  = $isPublished && !in_array($status, ['REDEEMED', 'SOLD', 'REMOVED'], true);

// ── Build photo_urls ─────────────────────────────────────────
$photoUrls = isset($data['photo_urls']) ? json_encode($data['photo_urls']) : null;

// ── Upsert (SQLite: INSERT OR REPLACE) ───────────────────────
try {
    $sql = "
        INSERT OR REPLACE INTO marketplace_items
            (remote_item_id, item_name, category, brand, condition_grade,
             estimated_market_value, marketplace_price, shop_location,
             shop_branch_name, shop_contact_phone, status, is_published,
             is_available, photo_urls, notes, updated_at_timestamp, updated_at)
        VALUES
            (:rid, :name, :cat, :brand, :cond,
             :emv, :price, :shop,
             :branch, :phone, :status, :published,
             :available, :photos, :notes, :ts, CURRENT_TIMESTAMP)
    ";

    $stmt = $db->prepare($sql);
    $stmt->execute([
        ':rid'       => (int)$data['remote_item_id'],
        ':name'      => $data['item_name'] ?? 'Unnamed Item',
        ':cat'       => $data['category'] ?? 'Other Electronics',
        ':brand'     => $data['brand'] ?? null,
        ':cond'      => $data['condition'] ?? $data['condition_grade'] ?? 'Good',
        ':emv'       => $data['estimated_market_value'] ?? null,
        ':price'     => $data['marketplace_price'] ?? 0,
        ':shop'      => $data['shop_location'] ?? 'Shop 1',
        ':branch'    => $data['shop_branch_name'] ?? null,
        ':phone'     => $data['shop_contact_phone'] ?? null,
        ':status'    => $status,
        ':published' => $isPublished ? 1 : 0,
        ':available' => $isAvailable ? 1 : 0,
        ':photos'    => $photoUrls,
        ':notes'     => $data['notes'] ?? null,
        ':ts'        => $data['updated_at_timestamp'] ?? null,
    ]);

    // ── Log sync ─────────────────────────────────────────────
    $db->prepare("INSERT INTO sync_log (action, item_id, payload, status) VALUES ('sync', ?, ?, 'ok')")
       ->execute([$data['remote_item_id'], $raw]);

    echo json_encode([
        'success'      => true,
        'remote_id'    => (int)$data['remote_item_id'],
        'is_available' => $isAvailable,
        'message'      => $isAvailable ? 'Item synced and live on marketplace.' : 'Item synced but marked unavailable.',
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    $db->prepare("INSERT INTO sync_log (action, item_id, payload, status, message) VALUES ('sync', ?, ?, 'error', ?)")
       ->execute([$data['remote_item_id'] ?? null, $raw, $e->getMessage()]);
    echo json_encode(['error' => 'Sync failed.', 'details' => $e->getMessage()]);
}
