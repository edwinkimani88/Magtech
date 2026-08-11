<?php
/**
 * MagTech — Database Setup Script (SQLite Compatible)
 */

$token = $_GET['token'] ?? '';
if ($token !== 'magtechsetup2026') {
    http_response_code(403);
    die('<h1>403 Forbidden</h1><p>Provide ?token=magtechsetup2026 to run setup.</p>');
}

define('DB_FILE', __DIR__ . '/config/magtech.sqlite');

echo '<!DOCTYPE html><html><head><title>MagTech Setup</title>';
echo '<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">';
echo '<style>body{font-family:Inter,sans-serif;max-width:700px;margin:3rem auto;padding:2rem;background:#f9f9f7}
h1{color:#06403F}.ok{color:#16a34a;font-weight:600}.err{color:#dc2626;font-weight:600}
.card{background:#fff;border:1px solid #e5e5e5;border-radius:16px;padding:2rem;margin-top:1.5rem}</style>';
echo '</head><body>';
echo '<h1>⚡ MagTech Database Setup (SQLite Engine)</h1>';

try {
    $pdo = new PDO("sqlite:" . DB_FILE, null, null, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    ]);

    echo '<p class="ok">✓ Connected to SQLite database file</p>';

    // Create items table
    $pdo->exec("
        CREATE TABLE IF NOT EXISTS marketplace_items (
          id                     INTEGER PRIMARY KEY AUTOINCREMENT,
          remote_item_id         INTEGER UNIQUE NOT NULL,
          item_name              TEXT NOT NULL,
          category               TEXT NOT NULL,
          brand                  TEXT DEFAULT NULL,
          condition_grade        TEXT DEFAULT 'Good',
          estimated_market_value REAL DEFAULT NULL,
          marketplace_price      REAL NOT NULL,
          shop_location          TEXT NOT NULL,
          shop_branch_name       TEXT DEFAULT NULL,
          shop_contact_phone     TEXT DEFAULT NULL,
          status                 TEXT DEFAULT 'FORFEITED',
          is_published           INTEGER DEFAULT 0,
          is_available           INTEGER DEFAULT 1,
          photo_urls             TEXT DEFAULT NULL,
          notes                  TEXT DEFAULT NULL,
          views_count            INTEGER DEFAULT 0,
          updated_at_timestamp   INTEGER DEFAULT NULL,
          created_at             DATETIME DEFAULT CURRENT_TIMESTAMP,
          updated_at             DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    ");
    echo '<p class="ok">✓ Table `marketplace_items` created</p>';

    // Create sync_log table
    $pdo->exec("
        CREATE TABLE IF NOT EXISTS sync_log (
          id         INTEGER PRIMARY KEY AUTOINCREMENT,
          action     TEXT NOT NULL,
          item_id    INTEGER DEFAULT NULL,
          payload    TEXT DEFAULT NULL,
          status     TEXT DEFAULT 'ok',
          message    TEXT DEFAULT NULL,
          created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    ");
    echo '<p class="ok">✓ Table `sync_log` created</p>';

    // Seed demo data
    $existing = (int)$pdo->query("SELECT COUNT(*) FROM marketplace_items")->fetchColumn();
    if ($existing === 0) {
        $pdo->exec("
            INSERT INTO marketplace_items
              (remote_item_id, item_name, category, brand, condition_grade, estimated_market_value, marketplace_price, shop_location, shop_branch_name, shop_contact_phone, status, is_published, is_available, photo_urls, notes)
            VALUES
              (1001,'Samsung Galaxy S23 Ultra','Phones','Samsung','Like New',95000,85000,'Shop 1','Shop 1 (Westlands)','+254712345678','FORFEITED',1,1,'[\"assets/img/product-phone.png\"]','Original charger included. Screen protector on. IMEI verified.'),
              (1002,'HP EliteBook 840 G8 i5','Laptops','HP','Good',65000,52000,'Shop 2','Shop 2 (CBD)','+254798765432','DIRECT_BUY',1,1,'[\"assets/img/product-laptop.png\"]','Fully functional. Battery health 87%. Charger included.'),
              (1003,'Sony Bravia 55\" 4K Smart TV','TVs & Audio','Sony','Like New',48000,42000,'Shop 1','Shop 1 (Westlands)','+254712345678','FORFEITED',1,1,'[\"assets/img/product-tv.png\"]','Remote included. WiFi tested. No scratches on screen.'),
              (1004,'Samsung Side-by-Side Fridge','Fridges & Appliances','Samsung','Good',75000,60000,'Shop 2','Shop 2 (CBD)','+254798765432','CONSIGNMENT',1,1,'[\"assets/img/product-fridge.png\"]','Frost-free. Water dispenser works. Clean interior.'),
              (1005,'Sony HT-S20R Home Theatre','TVs & Audio','Sony','Good',28000,22000,'Shop 1','Shop 1 (Westlands)','+254712345678','FORFEITED',1,1,'[\"assets/img/product-speakers.png\"]','All 5 speakers present. Subwoofer works fine. Remote included.'),
              (1006,'iPhone 14 Pro Max 256GB','Phones','Apple','Like New',120000,105000,'Shop 1','Shop 1 (Westlands)','+254712345678','FORFEITED',1,1,'[\"assets/img/product-phone.png\"]','iCloud unlocked. Face ID working. Battery 91%.'),
              (1007,'Dell Latitude 5520 i7','Laptops','Dell','Good',72000,60000,'Shop 2','Shop 2 (CBD)','+254798765432','DIRECT_BUY',1,1,'[\"assets/img/product-laptop.png\"]','16GB RAM, 512GB SSD. Fast and clean. Charger included.'),
              (1008,'LG 43\" Smart TV WebOS','TVs & Audio','LG','Fair',28000,22000,'Shop 2','Shop 2 (CBD)','+254798765432','FORFEITED',1,1,'[\"assets/img/product-tv.png\"]','Minor frame scratch. Screen perfect. Smart features working.'),
              (1009,'PlayStation 5 Console','Gaming','Sony','Good',75000,62000,'Shop 1','Shop 1 (Westlands)','+254712345678','CONSIGNMENT',1,1,'[\"assets/img/product-speakers.png\"]','2 controllers included. Disc edition. All cables present.'),
              (1010,'Hisense 310L Double Door Fridge','Fridges & Appliances','Hisense','Good',38000,32000,'Shop 2','Shop 2 (CBD)','+254798765432','DIRECT_BUY',1,1,'[\"assets/img/product-fridge.png\"]','Cooling perfect. Freezer working. Minor dents on side.'),
              (1011,'Samsung Galaxy Tab S8','Phones','Samsung','Like New',55000,48000,'Shop 1','Shop 1 (Westlands)','+254712345678','FORFEITED',1,1,'[\"assets/img/product-phone.png\"]','S Pen included. 128GB. WiFi + LTE. Screen immaculate.'),
              (1012,'JBL PartyBox 310 Speaker','TVs & Audio','JBL','Good',36000,29000,'Shop 2','Shop 2 (CBD)','+254798765432','FORFEITED',1,1,'[\"assets/img/product-speakers.png\"]','Full charge, sound tested 100%. Lights working. Handle intact.')
        ");
        echo '<p class="ok">✓ Seeded 12 demo products</p>';
    } else {
        echo '<p style="color:#d97706">⚠ Table already has ' . $existing . ' items — skipping seed</p>';
    }

    echo '<div class="card">';
    echo '<h2 style="color:#06403F;margin-top:0">🎉 Setup Complete!</h2>';
    echo '<p>Marketplace ready: <a href="/Magtech/" style="color:#06403F">http://localhost/Magtech/</a></p>';
    echo '</div>';

} catch (PDOException $e) {
    echo '<p class="err">✗ Database error: ' . htmlspecialchars($e->getMessage()) . '</p>';
}

echo '</body></html>';
