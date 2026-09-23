<?php
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/database.php';

/**
 * Perform a cURL request to the Supabase PostgREST API
 */
function supabaseApiRequest(string $endpoint, string $method = 'GET', ?array $data = null, bool $useSecretKey = false, array $extraHeaders = []): array {
    $url = rtrim(SUPABASE_URL, '/') . '/rest/v1/' . ltrim($endpoint, '/');
    $key = $useSecretKey ? SUPABASE_SECRET_KEY : SUPABASE_PUBLISHABLE_KEY;

    $headers = [
        'apikey: ' . $key,
        'Authorization: Bearer ' . $key,
        'Content-Type: application/json',
    ];

    foreach ($extraHeaders as $h) {
        $headers[] = $h;
    }

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_TIMEOUT, 6);
    curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 3);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
    curl_setopt($ch, CURLOPT_HEADER, true); // return headers for Content-Range

    if ($method === 'POST') {
        curl_setopt($ch, CURLOPT_POST, true);
        if ($data !== null) {
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
        }
    } elseif ($method === 'PATCH') {
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'PATCH');
        if ($data !== null) {
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
        }
    } elseif ($method === 'DELETE') {
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'DELETE');
    }

    $response = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $headerSize = curl_getinfo($ch, CURLINFO_HEADER_SIZE);
    $curlError = curl_error($ch);
    curl_close($ch);

    if ($response === false) {
        return [
            'success' => false,
            'status'  => 0,
            'error'   => $curlError,
            'data'    => null,
            'headers' => [],
        ];
    }

    $rawHeaders = substr($response, 0, $headerSize);
    $body = substr($response, $headerSize);

    // Parse headers
    $parsedHeaders = [];
    foreach (explode("\r\n", $rawHeaders) as $line) {
        if (str_contains($line, ':')) {
            [$k, $v] = explode(':', $line, 2);
            $parsedHeaders[strtolower(trim($k))] = trim($v);
        }
    }

    $decoded = json_decode($body, true);

    return [
        'success' => $httpCode >= 200 && $httpCode < 300,
        'status'  => $httpCode,
        'error'   => $httpCode >= 400 ? ($decoded['message'] ?? $body) : null,
        'data'    => $decoded,
        'headers' => $parsedHeaders,
        'raw'     => $body,
    ];
}

/**
 * Get category fallback photo
 */
function defaultCategoryPhoto(string $category): string {
    $cat = strtolower($category);
    return match (true) {
        str_contains($cat, 'phone')   => 'assets/img/product-phone-real.png',
        str_contains($cat, 'laptop')  => 'assets/img/product-laptop-real.png',
        str_contains($cat, 'tv')      => 'assets/img/product-tv-real.png',
        str_contains($cat, 'audio')   => 'assets/img/product-audio-real.png',
        str_contains($cat, 'fridge')  => 'assets/img/product-fridge-real.png',
        str_contains($cat, 'cook')    => 'assets/img/product-cooker-real.png',
        str_contains($cat, 'kitchen') => 'assets/img/product-kitchen-real.png',
        str_contains($cat, 'game') || str_contains($cat, 'gaming') => 'assets/img/product-gaming-real.png',
        default                       => 'assets/img/product-phone-real.png',
    };
}

/**
 * Normalize Supabase row to Magtech marketplace item structure
 */
function supabaseNormalizeItem(array $row): array {
    $id = (int)($row['id'] ?? 0);
    $shop = $row['shop_location'] ?? 'Shop 1';

    // Parse photo URLs
    $photos = [];
    $rawPhotos = $row['photo_urls_json'] ?? '';
    if (!empty($rawPhotos)) {
        if (is_array($rawPhotos)) {
            $photos = $rawPhotos;
        } else {
            $decoded = json_decode($rawPhotos, true);
            if (is_array($decoded)) {
                $photos = $decoded;
            } elseif (is_string($rawPhotos) && str_starts_with(trim($rawPhotos), '[')) {
                $photos = json_decode(trim($rawPhotos), true) ?: [];
            } elseif (!empty($rawPhotos)) {
                $photos = [$rawPhotos];
            }
        }
    }

    if (empty($photos)) {
        $photos = [defaultCategoryPhoto($row['category'] ?? '')];
    }

    $isPublished = !empty($row['is_published_to_marketplace']);
    $status = $row['status'] ?? 'Listed';
    $isAvailable = $isPublished && !in_array(strtoupper($status), ['SOLD', 'REDEEMED', 'REMOVED', 'DISPOSED'], true);

    $shops = SHOPS;
    $shopInfo = $shops[$shop] ?? [
        'name'     => $shop . ' (Kitengela)',
        'location' => 'Kitengela',
        'phone'    => '+254712345678',
    ];

    return [
        'id'                     => $id,
        'remote_item_id'         => $id,
        'item_name'              => $row['item_name'] ?? 'Unnamed Item',
        'category'               => $row['category'] ?? 'Other',
        'brand'                  => $row['brand'] ?? '',
        'condition_grade'        => ucwords(strtolower($row['condition'] ?? 'Good')),
        'estimated_market_value' => (float)($row['estimated_market_value'] ?? 0),
        'marketplace_price'      => (float)($row['marketplace_price'] ?? 0),
        'shop_location'          => $shop,
        'shop_branch_name'       => $shopInfo['name'],
        'shop_contact_phone'     => $shopInfo['phone'],
        'status'                 => $status,
        'is_published'           => $isPublished ? 1 : 0,
        'is_available'           => $isAvailable ? 1 : 0,
        'photo_urls'             => $photos,
        'notes'                  => $row['notes'] ?? '',
        'views_count'            => (int)($row['views_count'] ?? 0),
        'created_at'             => $row['created_at'] ?? date('Y-m-d H:i:s'),
        'updated_at'             => $row['updated_at'] ?? date('Y-m-d H:i:s'),
    ];
}

/**
 * Fetch items from Supabase with filters, sorting, and pagination
 */
function supabaseFetchItems(array $options = []): array {
    $category  = trim($options['category'] ?? 'All');
    $shopLoc   = trim($options['shop_location'] ?? 'All');
    $search    = trim($options['search'] ?? '');
    $sort      = $options['sort'] ?? 'newest';
    $limit     = max(1, (int)($options['limit'] ?? ITEMS_PER_PAGE));
    $offset    = max(0, (int)($options['offset'] ?? 0));

    // PostgREST query parameters
    $params = [
        'select' => '*',
        'is_published_to_marketplace' => 'eq.true',
    ];

    // Filter by category (handles compound categories e.g. "TVs & Audio")
    if ($category !== 'All' && $category !== '') {
        if ($category === 'TVs & Audio') {
            $params['category'] = 'in.(TVs,Audio)';
        } elseif ($category === 'Fridges & Appliances') {
            $params['category'] = 'in.(Fridges,Cookers,Home Appliances)';
        } else {
            $params['category'] = 'eq.' . urlencode($category);
        }
    }

    // Filter by branch
    if ($shopLoc !== 'All' && $shopLoc !== '') {
        $params['shop_location'] = 'eq.' . urlencode($shopLoc);
    }

    // Search query
    if ($search !== '') {
        $searchEsc = urlencode('*' . $search . '*');
        $params['or'] = "(item_name.ilike.{$searchEsc},brand.ilike.{$searchEsc},notes.ilike.{$searchEsc})";
    }

    // Order
    $orderMap = [
        'price_asc'  => 'marketplace_price.asc',
        'price_desc' => 'marketplace_price.desc',
        'popular'    => 'id.desc',
        'newest'     => 'id.desc',
    ];
    $params['order'] = $orderMap[$sort] ?? 'id.desc';
    $params['limit'] = (string)$limit;
    $params['offset'] = (string)$offset;

    // Build query string
    $queryString = http_build_query($params);

    // Request with count=exact header to get total count
    $res = supabaseApiRequest('items?' . $queryString, 'GET', null, false, ['Prefer: count=exact']);

    if ($res['success'] && is_array($res['data'])) {
        $items = array_map('supabaseNormalizeItem', $res['data']);

        // Parse total from content-range header e.g. "0-9/42"
        $total = count($items);
        if (isset($res['headers']['content-range'])) {
            $parts = explode('/', $res['headers']['content-range']);
            if (isset($parts[1]) && is_numeric($parts[1])) {
                $total = (int)$parts[1];
            }
        }

        // Cache into local SQLite for fallback
        try {
            supabaseSyncCache($items);
        } catch (\Throwable $e) {}

        return [
            'success' => true,
            'items'   => $items,
            'total'   => $total,
            'source'  => 'supabase_live',
        ];
    }

    // Fallback to local SQLite cache if network issue
    return supabaseFetchFromLocalFallback($options);
}

/**
 * Fetch a single item from Supabase by ID
 */
function supabaseGetItemById(int|string $id): ?array {
    $id = (int)$id;
    if ($id <= 0) return null;

    $res = supabaseApiRequest("items?id=eq.{$id}&select=*&limit=1");
    if ($res['success'] && !empty($res['data'][0])) {
        $item = supabaseNormalizeItem($res['data'][0]);
        // Also update local cache
        try {
            supabaseSyncCache([$item]);
        } catch (\Throwable $e) {}
        return $item;
    }

    // Fallback to SQLite
    try {
        $db = getDB();
        $stmt = $db->prepare("SELECT * FROM marketplace_items WHERE id = ? OR remote_item_id = ? LIMIT 1");
        $stmt->execute([$id, $id]);
        $row = $stmt->fetch();
        if ($row) {
            $row['photo_urls'] = json_decode($row['photo_urls'] ?? '[]', true) ?: [defaultCategoryPhoto($row['category'] ?? '')];
            return $row;
        }
    } catch (\Throwable $e) {}

    return null;
}

/**
 * Get category counts directly from Supabase
 */
function supabaseGetCategoryCounts(): array {
    $res = supabaseApiRequest('items?select=category&is_published_to_marketplace=eq.true');
    if ($res['success'] && is_array($res['data'])) {
        $counts = [];
        foreach ($res['data'] as $row) {
            $c = $row['category'] ?? 'Other';
            $counts[$c] = ($counts[$c] ?? 0) + 1;
        }
        return $counts;
    }

    // Fallback to SQLite
    try {
        $db = getDB();
        $cstmt = $db->query("SELECT category, COUNT(*) as cnt FROM marketplace_items WHERE is_published=1 GROUP BY category");
        return $cstmt->fetchAll(PDO::FETCH_KEY_PAIR) ?: [];
    } catch (\Throwable $e) {
        return [];
    }
}

/**
 * Sync Supabase items into local SQLite database for instant fallback
 */
function supabaseSyncCache(array $items): void {
    if (empty($items)) return;
    try {
        $db = getDB();
        $stmt = $db->prepare("
            INSERT OR REPLACE INTO marketplace_items
                (id, remote_item_id, item_name, category, brand, condition_grade,
                 estimated_market_value, marketplace_price, shop_location,
                 shop_branch_name, shop_contact_phone, status, is_published,
                 is_available, photo_urls, notes, updated_at)
            VALUES
                (:id, :rid, :name, :cat, :brand, :cond,
                 :emv, :price, :shop,
                 :branch, :phone, :status, :pub,
                 :avail, :photos, :notes, CURRENT_TIMESTAMP)
        ");

        foreach ($items as $item) {
            $photos = is_array($item['photo_urls']) ? json_encode($item['photo_urls']) : ($item['photo_urls'] ?? '[]');
            $stmt->execute([
                ':id'     => $item['id'],
                ':rid'    => $item['remote_item_id'] ?? $item['id'],
                ':name'   => $item['item_name'],
                ':cat'    => $item['category'],
                ':brand'  => $item['brand'] ?? '',
                ':cond'   => $item['condition_grade'] ?? 'Good',
                ':emv'    => $item['estimated_market_value'] ?? 0,
                ':price'  => $item['marketplace_price'] ?? 0,
                ':shop'   => $item['shop_location'] ?? 'Shop 1',
                ':branch' => $item['shop_branch_name'] ?? 'Shop 1 (Chairman)',
                ':phone'  => $item['shop_contact_phone'] ?? '+254712345678',
                ':status' => $item['status'] ?? 'Listed',
                ':pub'    => $item['is_published'] ?? 1,
                ':avail'  => $item['is_available'] ?? 1,
                ':photos' => $photos,
                ':notes'  => $item['notes'] ?? '',
            ]);
        }
    } catch (\Throwable $e) {
        // Silently ignore cache write errors
    }
}

/**
 * Fallback query from local SQLite
 */
function supabaseFetchFromLocalFallback(array $options = []): array {
    try {
        $db = getDB();
        $category  = trim($options['category'] ?? 'All');
        $shopLoc   = trim($options['shop_location'] ?? 'All');
        $search    = trim($options['search'] ?? '');
        $sort      = $options['sort'] ?? 'newest';
        $limit     = max(1, (int)($options['limit'] ?? ITEMS_PER_PAGE));
        $offset    = max(0, (int)($options['offset'] ?? 0));

        $where  = ['is_published = 1'];
        $params = [];

        if ($category !== 'All' && $category !== '') {
            $where[] = 'category = :cat';
            $params[':cat'] = $category;
        }
        if ($shopLoc !== 'All' && $shopLoc !== '') {
            $where[] = 'shop_location = :shop';
            $params[':shop'] = $shopLoc;
        }
        if ($search !== '') {
            $where[] = '(item_name LIKE :s OR brand LIKE :s OR notes LIKE :s)';
            $params[':s'] = '%' . $search . '%';
        }

        $whereSQL = implode(' AND ', $where);
        $orderSQL = match ($sort) {
            'price_asc'  => 'marketplace_price ASC',
            'price_desc' => 'marketplace_price DESC',
            'popular'    => 'views_count DESC',
            default      => 'id DESC',
        };

        $cstmt = $db->prepare("SELECT COUNT(*) FROM marketplace_items WHERE $whereSQL");
        $cstmt->execute($params);
        $total = (int)$cstmt->fetchColumn();

        $stmt = $db->prepare("SELECT * FROM marketplace_items WHERE $whereSQL ORDER BY $orderSQL LIMIT :lim OFFSET :off");
        foreach ($params as $k => $v) $stmt->bindValue($k, $v);
        $stmt->bindValue(':lim', $limit, PDO::PARAM_INT);
        $stmt->bindValue(':off', $offset, PDO::PARAM_INT);
        $stmt->execute();
        $items = $stmt->fetchAll();

        foreach ($items as &$item) {
            $item['photo_urls'] = json_decode($item['photo_urls'] ?? '[]', true) ?: [defaultCategoryPhoto($item['category'] ?? '')];
        }

        return [
            'success' => true,
            'items'   => $items,
            'total'   => $total,
            'source'  => 'sqlite_fallback',
        ];
    } catch (\Throwable $e) {
        return ['success' => false, 'items' => [], 'total' => 0, 'error' => $e->getMessage()];
    }
}
