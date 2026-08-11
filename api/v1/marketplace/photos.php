<?php
require_once __DIR__ . '/../../../config/config.php';
require_once __DIR__ . '/../../../config/database.php';

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: ' . CORS_ORIGIN);
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, X-MagTech-Api-Key');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') { http_response_code(200); exit; }

// Auth
$apiKey = $_SERVER['HTTP_X_MAGTECH_API_KEY'] ?? '';
if ($apiKey !== MAGTECH_API_KEY) {
    http_response_code(401);
    echo json_encode(['error' => 'Unauthorised.']);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['error' => 'Method not allowed.']);
    exit;
}

$remoteId = $_POST['remote_item_id'] ?? null;
if (!$remoteId) {
    http_response_code(400);
    echo json_encode(['error' => 'remote_item_id required.']);
    exit;
}

$uploaded = [];
$uploadDir = UPLOAD_PATH;

if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0755, true);
}

$allowedMimes = ['image/jpeg', 'image/png', 'image/webp'];

foreach ($_FILES as $fileKey => $file) {
    if ($file['error'] !== UPLOAD_ERR_OK) continue;
    if (!in_array(mime_content_type($file['tmp_name']), $allowedMimes, true)) continue;

    $ext      = pathinfo($file['name'], PATHINFO_EXTENSION) ?: 'jpg';
    $filename = 'item_' . $remoteId . '_' . uniqid() . '.' . strtolower($ext);
    $dest     = $uploadDir . $filename;

    if (move_uploaded_file($file['tmp_name'], $dest)) {
        $uploaded[] = UPLOAD_URL . $filename;
    }
}

if (empty($uploaded)) {
    http_response_code(400);
    echo json_encode(['error' => 'No valid images uploaded.']);
    exit;
}

// Append to existing photo_urls for the item
$db   = getDB();
$stmt = $db->prepare("SELECT photo_urls FROM marketplace_items WHERE remote_item_id = ?");
$stmt->execute([$remoteId]);
$row  = $stmt->fetch();

if ($row) {
    $existing = json_decode($row['photo_urls'] ?? '[]', true) ?: [];
    $merged   = array_values(array_unique(array_merge($existing, $uploaded)));
    $db->prepare("UPDATE marketplace_items SET photo_urls = ? WHERE remote_item_id = ?")
       ->execute([json_encode($merged), $remoteId]);
} else {
    $merged = $uploaded;
}

echo json_encode([
    'success'    => true,
    'uploaded'   => $uploaded,
    'total_photos' => count($merged),
]);
