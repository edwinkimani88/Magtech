<?php
/**
 * MagTech — Database Setup Script (SQLite, Realistic Kenyan Catalogue)
 * URL: /setup?token=magtechsetup2026
 */

$token = $_GET['token'] ?? '';
if ($token !== 'magtechsetup2026') {
    http_response_code(403);
    die('<h1>403 Forbidden</h1><p>Provide ?token=magtechsetup2026 to run setup.</p>');
}

$force = isset($_GET['force']) && $_GET['force'] === '1';

define('DB_FILE', __DIR__ . '/config/magtech.sqlite');

echo '<!DOCTYPE html><html><head><title>MagTech Setup</title>';
echo '<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">';
echo '<style>body{font-family:Inter,sans-serif;max-width:760px;margin:3rem auto;padding:2rem;background:#f4f3f0}
h1{color:#06403F}.ok{color:#16a34a;font-weight:600}.err{color:#dc2626;font-weight:600}.warn{color:#d97706}
.card{background:#fff;border:1px solid #e5e5e5;border-radius:16px;padding:2rem;margin-top:1.5rem}
pre{background:#1e1e1e;color:#d4d4d4;padding:1rem;border-radius:8px;overflow:auto;font-size:13px}</style>';
echo '</head><body>';
echo '<h1>⚡ MagTech Database Setup (SQLite Engine)</h1>';

try {
    $pdo = new PDO("sqlite:" . DB_FILE, null, null, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    ]);
    $pdo->exec("PRAGMA foreign_keys = ON;");
    $pdo->exec("PRAGMA journal_mode = WAL;");

    echo '<p class="ok">✓ Connected to SQLite database</p>';

    // ── Create tables ───────────────────────────────────────────────
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
    echo '<p class="ok">✓ Table `marketplace_items` ready</p>';

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
    echo '<p class="ok">✓ Table `sync_log` ready</p>';

    // ── Seed realistic Kenyan catalogue ────────────────────────────
    $existing = (int)$pdo->query("SELECT COUNT(*) FROM marketplace_items")->fetchColumn();

    if ($existing > 0 && !$force) {
        echo "<p class='warn'>⚠ Table already has {$existing} items. Add ?force=1 to re-seed.</p>";
    } else {
        if ($force && $existing > 0) {
            $pdo->exec("DELETE FROM marketplace_items");
            echo '<p class="ok">✓ Cleared old catalogue</p>';
        }

        // Image paths (realistic photographs)
        $ph   = '["assets/img/product-phone-real.png"]';
        $tv   = '["assets/img/product-tv-real.png"]';
        $au   = '["assets/img/product-audio-real.png"]';
        $ck   = '["assets/img/product-cooker-real.png"]';
        $fr   = '["assets/img/product-fridge-real.png"]';
        $lp   = '["assets/img/product-laptop-real.png"]';
        $gm   = '["assets/img/product-gaming-real.png"]';
        $kt   = '["assets/img/product-kitchen-real.png"]';

        /*
         * Columns:
         * remote_item_id | item_name | category | brand | condition_grade
         * | estimated_market_value | marketplace_price | shop_location
         * | shop_branch_name | shop_contact_phone | status
         * | is_published | is_available | photo_urls | notes
         */
        $products = [
            // ── PHONES ──────────────────────────────────────────────────────
            [2001,'Tecno Camon 20 — 8GB/256GB','Phones','Tecno','Good',
             15000,11500,'Shop 1','Shop 1 (Chairman)','+254712345678','FORFEITED',1,1,$ph,
             'Fully functional. Screen has minor hairline scratch. Charger included. IMEI clear.'],
            [2002,'Infinix Hot 12 Play — 4GB/128GB','Phones','Infinix','Fair',
             9000,6500,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','DIRECT_BUY',1,1,$ph,
             'Works perfectly. Battery drains a bit faster than new — still lasts a day with normal use. Charger included.'],
            [2003,'Samsung Galaxy A23 5G','Phones','Samsung','Good',
             20000,14000,'Shop 1','Shop 1 (Chairman)','+254712345678','FORFEITED',1,1,$ph,
             'Minor frame scratches, screen clean. IMEI verified. Original charger and box available.'],
            [2004,'Nokia 6300 4G Button Phone','Phones','Nokia','Like New',
             4500,2500,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','CONSIGNMENT',1,1,$ph,
             'Works like new. Small and light — ideal as a second/backup phone. Charger and box present.'],
            [2005,'Itel P38 Pro — 3GB/64GB','Phones','Itel','Good',
             7500,4800,'Shop 1','Shop 1 (Chairman)','+254712345678','DIRECT_BUY',1,1,$ph,
             'Good battery life. No visible screen damage. Charger included. Ideal budget smartphone.'],
            [2006,'Samsung Galaxy A14','Phones','Samsung','Like New',
             17000,13000,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','FORFEITED',1,1,$ph,
             'Screen protector on. No scratches. IMEI clean. Comes with charger and earphones in original box.'],
            [2007,'Tecno Spark 10 Pro — 8GB/256GB','Phones','Tecno','Good',
             16500,12000,'Shop 1','Shop 1 (Chairman)','+254712345678','FORFEITED',1,1,$ph,
             'Camera working excellently. Minor back casing scuff. Screen clean. IMEI verified.'],
            [2008,'Infinix Note 12 — 6GB/128GB','Phones','Infinix','Fair',
             12000,7500,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','DIRECT_BUY',1,1,$ph,
             'Side button worn. Screen works fine, slight discolouration on edge. Good for everyday use.'],
            [2009,'Nokia 3310 (Dual SIM Button Phone)','Phones','Nokia','Good',
             2500,1200,'Shop 1','Shop 1 (Chairman)','+254712345678','CONSIGNMENT',1,1,$ph,
             'Classic reliable phone. Long battery life. Great for a backup or elderly relative. Charger included.'],
            [2010,'Samsung Galaxy S21 FE — 6GB/128GB','Phones','Samsung','Good',
             55000,22000,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','FORFEITED',1,1,$ph,
             'IMEI clear. Screen is excellent. Some wear on frame. Comes with charger.'],

            // ── TELEVISIONS ──────────────────────────────────────────────────
            [2011,'Hisense 32" Smart TV (Android)','TVs & Audio','Hisense','Good',
             19000,14500,'Shop 1','Shop 1 (Chairman)','+254712345678','DIRECT_BUY',1,1,$tv,
             'Smart Android TV. YouTube, Netflix ready. Remote included. Minor frame scratch on base.'],
            [2012,'Vitron 24" HD LED TV','TVs & Audio','Vitron','Fair',
             10000,6500,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','FORFEITED',1,1,$tv,
             'Picture quality good. Minor scratch on plastic frame. Remote present. HDMI working.'],
            [2013,'LG 43" Smart TV (WebOS)','TVs & Audio','LG','Good',
             40000,28000,'Shop 1','Shop 1 (Chairman)','+254712345678','CONSIGNMENT',1,1,$tv,
             'All smart features working. Screen in great shape. Remote and wall bracket included.'],
            [2014,'Samsung 40" Full HD TV','TVs & Audio','Samsung','Good',
             30000,19000,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','FORFEITED',1,1,$tv,
             'Powerful picture. USB and HDMI ports working. Remote included. Stand slightly wobbly — wall mount recommended.'],
            [2015,'TCL 55" Smart 4K TV','TVs & Audio','TCL','Good',
             60000,34000,'Shop 1','Shop 1 (Chairman)','+254712345678','DIRECT_BUY',1,1,$tv,
             'Large screen at a good price. Remote + mount included. Some pixel fade on bottom edge — barely noticeable in use.'],
            [2016,'Bruhm 32" TV (Non-Smart)','TVs & Audio','Bruhm','Fair',
             8000,4500,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','CONSIGNMENT',1,1,$tv,
             'Basic no-frills TV. Remote missing — universal remote compatible. Screen clean. Good for bedroom or kitchen.'],

            // ── AUDIO ─────────────────────────────────────────────────────────
            [2017,'Sony 5.1 Home Theatre System','TVs & Audio','Sony','Good',
             22000,14000,'Shop 1','Shop 1 (Chairman)','+254712345678','FORFEITED',1,1,$au,
             'All 5 satellite speakers present plus subwoofer. Bluetooth working. Remote included. Tested and sounds great.'],
            [2018,'Sayona 3.1 Woofer System','TVs & Audio','Sayona','Good',
             8500,5800,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','DIRECT_BUY',1,1,$au,
             'Loud and crisp sound. USB/FM/Bluetooth working. Remote present. Minor scuff on subwoofer.'],
            [2019,'JBL Go 3 Portable Speaker','TVs & Audio','JBL','Like New',
             7000,4800,'Shop 1','Shop 1 (Chairman)','+254712345678','CONSIGNMENT',1,1,$au,
             'Compact waterproof Bluetooth speaker. Battery like new. USB-C charging cable included. No scratches.'],
            [2020,'Amtec Bluetooth Mini Woofer Speaker','TVs & Audio','Amtec','Fair',
             5000,2800,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','DIRECT_BUY',1,1,$au,
             'USB and Bluetooth work. One satellite speaker has a slight rattle at very high volume. Otherwise functional.'],
            [2021,'Samsung Soundbar 2.1ch (HW-T450)','TVs & Audio','Samsung','Good',
             18000,11000,'Shop 1','Shop 1 (Chairman)','+254712345678','FORFEITED',1,1,$au,
             'Powerful bass. Subwoofer wireless connected. Remote included. HDMI ARC working. Tested.'],

            // ── LAPTOPS ───────────────────────────────────────────────────────
            [2022,'HP ProBook 450 G6 (Core i5)','Laptops','HP','Good',
             45000,35000,'Shop 1','Shop 1 (Chairman)','+254712345678','DIRECT_BUY',1,1,$lp,
             '8GB RAM, 256GB SSD. Windows 11. Battery 80%. Charger included. Keyboard worn but fully functional.'],
            [2023,'Dell Inspiron 15 3000 (Core i3)','Laptops','Dell','Fair',
             25000,16000,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','FORFEITED',1,1,$lp,
             'Screen intact. Keyboard functional. Battery replacement needed — stays on charger all day. 4GB RAM, 500GB HDD.'],
            [2024,'Lenovo ThinkPad E14 (Core i5)','Laptops','Lenovo','Good',
             50000,38000,'Shop 1','Shop 1 (Chairman)','+254712345678','CONSIGNMENT',1,1,$lp,
             '8GB RAM, 256GB SSD. Battery health 75%. Sturdy business-grade laptop. Charger included.'],
            [2025,'ASUS VivoBook 15 (Core i5)','Laptops','ASUS','Like New',
             55000,42000,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','FORFEITED',1,1,$lp,
             'Barely used. 8GB RAM, 512GB SSD. 15.6" FHD display. Windows 11. Original box and charger.'],

            // ── KITCHEN ──────────────────────────────────────────────────────
            [2026,'Ramtons 2-Burner Gas Cooker (Tabletop)','Kitchen','Ramtons','Good',
             7500,5200,'Shop 1','Shop 1 (Chairman)','+254712345678','DIRECT_BUY',1,1,$ck,
             'Flame ignition working on both burners. Minor discolouration on grates from use. LPG hose included.'],
            [2027,'Von Hotpoint Electric Cooker (2-Plate)','Kitchen','Von','Fair',
             5000,3000,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','FORFEITED',1,1,$ck,
             'Both plates heat up. One plate slower than the other. Good for basic cooking needs. Plug working.'],
            [2028,'Cosori 5.5L Air Fryer','Kitchen','Cosori','Good',
             10000,7000,'Shop 1','Shop 1 (Chairman)','+254712345678','CONSIGNMENT',1,1,$kt,
             'Timer and temperature controls work perfectly. Basket clean. Tested. Minor scratch on exterior.'],
            [2029,'Breville Mini Air Fryer 3L','Kitchen','Breville','Like New',
             8000,5500,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','DIRECT_BUY',1,1,$kt,
             'Very light use. Basket like new. No visible marks. Perfect for a small household.'],
            [2030,'Blueflame Pressure Cooker 5L','Kitchen','Blueflame','Good',
             4500,2500,'Shop 1','Shop 1 (Chairman)','+254712345678','FORFEITED',1,1,$kt,
             'Seal gasket intact. Lid locks properly. Great for beans and ugali. Slight staining on exterior.'],
            [2031,'Nasco 20L Microwave Oven','Kitchen','Nasco','Good',
             12000,7500,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','DIRECT_BUY',1,1,$kt,
             'All heating functions work. Timer accurate. Turntable present. Minor interior scuff. Plug tested.'],

            // ── FRIDGES & APPLIANCES ──────────────────────────────────────────
            [2032,'Hisense 150L Single Door Fridge','Fridges & Appliances','Hisense','Good',
             20000,16000,'Shop 1','Shop 1 (Chairman)','+254712345678','FORFEITED',1,1,$fr,
             'Cools perfectly. Interior clean. Door seal intact. Minor dent on side panel. Tested and working.'],
            [2033,'Samsung 260L Double Door Fridge','Fridges & Appliances','Samsung','Good',
             45000,30000,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','DIRECT_BUY',1,1,$fr,
             'Freezer and fridge compartments both working. Interior in good condition. Minor surface rust on handle.'],
            [2034,'Von 92L Bar Fridge (Mini Fridge)','Fridges & Appliances','Von','Fair',
             10000,6500,'Shop 1','Shop 1 (Chairman)','+254712345678','CONSIGNMENT',1,1,$fr,
             'Cools well. Ideal for a room or small apartment. Slight scratch on door. Interior clean.'],

            // ── GAMING ───────────────────────────────────────────────────────
            [2035,'PlayStation 4 Slim 500GB','Gaming','Sony','Good',
             28000,20000,'Shop 1','Shop 1 (Chairman)','+254712345678','FORFEITED',1,1,$gm,
             'Console and 2 controllers. Cables and power adapter included. Tested on FIFA25. Minor controller stick drift on one unit.'],
            [2036,'Xbox One S 1TB','Gaming','Microsoft','Good',
             22000,15000,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','DIRECT_BUY',1,1,$gm,
             'Full set: console, 1 controller, HDMI cable, power brick. Controller battery cover missing. Otherwise fully functional.'],
            [2037,'Nintendo Switch (Grey) + Dock','Gaming','Nintendo','Like New',
             35000,26000,'Shop 1','Shop 1 (Chairman)','+254712345678','CONSIGNMENT',1,1,$gm,
             'Screen no scratches. Dock, cables, and both Joy-Cons present. Battery holds 4+ hours. Tested.'],
            [2038,'Ozone 2.1 Gaming Speaker + Subwoofer','Gaming','Ozone','Good',
             8500,5500,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','FORFEITED',1,1,$gm,
             'RGB lighting working. USB + 3.5mm inputs both functional. Bass strong. Minor scuff on back.'],

            // ── HOME APPLIANCES ───────────────────────────────────────────────
            [2039,'Ramtons Automatic Washing Machine 7kg','Fridges & Appliances','Ramtons','Good',
             30000,20000,'Shop 1','Shop 1 (Chairman)','+254712345678','DIRECT_BUY',1,1,$fr,
             'All wash cycles working. Tested with full load. Slight laundry residue smell — quick hot wash cleans it. Hose included.'],
            [2040,'Tornado Stand Fan 16"','Fridges & Appliances','Tornado','Fair',
             4000,2200,'Shop 2','Shop 2 (Deliverance Road)','+254798765432','CONSIGNMENT',1,1,$fr,
             'All 3 speed settings work. Oscillation works. Fan guard slightly bent but does not affect use.'],
        ];

        $stmt = $pdo->prepare("
            INSERT OR REPLACE INTO marketplace_items
               (remote_item_id, item_name, category, brand, condition_grade,
                estimated_market_value, marketplace_price, shop_location,
                shop_branch_name, shop_contact_phone, status,
                is_published, is_available, photo_urls, notes)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        ");

        $count = 0;
        foreach ($products as $p) {
            $stmt->execute($p);
            $count++;
        }

        echo "<p class='ok'>✓ Seeded {$count} realistic Kenyan second-hand products</p>";
    }

    echo '<div class="card">';
    echo '<h2 style="color:#06403F;margin-top:0">🎉 Setup Complete!</h2>';
    echo '<p>Total items in DB: <strong>' . (int)$pdo->query("SELECT COUNT(*) FROM marketplace_items")->fetchColumn() . '</strong></p>';
    echo '<p>Marketplace: <a href="/Magtech/" style="color:#06403F">http://localhost/Magtech/</a></p>';
    echo '<p style="font-size:.85rem;color:#666">Re-seed with fresh data: <code>/setup?token=magtechsetup2026&force=1</code></p>';
    echo '</div>';

} catch (PDOException $e) {
    echo '<p class="err">✗ Database error: ' . htmlspecialchars($e->getMessage()) . '</p>';
}

echo '</body></html>';
