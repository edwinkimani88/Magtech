<?php
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../includes/supabase.php';

echo "Seeding products to Supabase (" . SUPABASE_URL . ")...\n";

$products = [
    // Phones
    [
        'item_name' => 'Tecno Camon 20 (8GB RAM, 256GB)',
        'category' => 'Phones',
        'brand' => 'Tecno',
        'condition' => 'GOOD',
        'estimated_market_value' => 15000,
        'forced_sale_value' => 10000,
        'marketplace_price' => 11500,
        'shop_location' => 'Shop 1',
        'status' => 'Listed',
        'entry_type' => 'DIRECT_PURCHASE',
        'is_published_to_marketplace' => true,
        'notes' => 'Minor hairline scratch. Charger included. IMEI verified clear.',
        'photo_urls_json' => '["assets/img/product-phone-real.png"]'
    ],
    [
        'item_name' => 'Samsung Galaxy A23 5G (128GB)',
        'category' => 'Phones',
        'brand' => 'Samsung',
        'condition' => 'LIKE NEW',
        'estimated_market_value' => 20000,
        'forced_sale_value' => 13000,
        'marketplace_price' => 14000,
        'shop_location' => 'Shop 2',
        'status' => 'Listed',
        'entry_type' => 'LOAN',
        'is_published_to_marketplace' => true,
        'notes' => 'Screen pristine. Original fast charger and box included.',
        'photo_urls_json' => '["assets/img/product-phone-real.png"]'
    ],
    [
        'item_name' => 'Infinix Note 12 (6GB/128GB)',
        'category' => 'Phones',
        'brand' => 'Infinix',
        'condition' => 'FAIR',
        'estimated_market_value' => 12000,
        'forced_sale_value' => 6000,
        'marketplace_price' => 7500,
        'shop_location' => 'Shop 1',
        'status' => 'Listed',
        'entry_type' => 'CONSIGNMENT',
        'is_published_to_marketplace' => true,
        'notes' => 'Good for daily use. Battery strong, minor cosmetic wear on frame.',
        'photo_urls_json' => '["assets/img/product-phone-real.png"]'
    ],
    [
        'item_name' => 'Samsung Galaxy S21 FE 5G (128GB)',
        'category' => 'Phones',
        'brand' => 'Samsung',
        'condition' => 'GOOD',
        'estimated_market_value' => 55000,
        'forced_sale_value' => 18000,
        'marketplace_price' => 22000,
        'shop_location' => 'Shop 2',
        'status' => 'Listed',
        'entry_type' => 'DIRECT_PURCHASE',
        'is_published_to_marketplace' => true,
        'notes' => 'Super fast flagship phone. 120Hz AMOLED. Charger included.',
        'photo_urls_json' => '["assets/img/product-phone-real.png"]'
    ],

    // Laptops
    [
        'item_name' => 'HP ProBook 450 G6 (Core i5, 8GB, 256GB SSD)',
        'category' => 'Laptops',
        'brand' => 'HP',
        'condition' => 'GOOD',
        'estimated_market_value' => 45000,
        'forced_sale_value' => 28000,
        'marketplace_price' => 35000,
        'shop_location' => 'Shop 1',
        'status' => 'Listed',
        'entry_type' => 'DIRECT_PURCHASE',
        'is_published_to_marketplace' => true,
        'notes' => 'Windows 11 Pro activated. Battery health 84%. Original HP charger.',
        'photo_urls_json' => '["assets/img/product-laptop-real.png"]'
    ],
    [
        'item_name' => 'Lenovo ThinkPad E14 (Core i5 10th Gen, 16GB RAM)',
        'category' => 'Laptops',
        'brand' => 'Lenovo',
        'condition' => 'GOOD',
        'estimated_market_value' => 50000,
        'forced_sale_value' => 30000,
        'marketplace_price' => 38000,
        'shop_location' => 'Shop 2',
        'status' => 'Listed',
        'entry_type' => 'LOAN',
        'is_published_to_marketplace' => true,
        'notes' => 'Solid business machine. Backlit keyboard, crisp FHD display.',
        'photo_urls_json' => '["assets/img/product-laptop-real.png"]'
    ],
    [
        'item_name' => 'ASUS VivoBook 15 (Core i5 11th Gen, 512GB SSD)',
        'category' => 'Laptops',
        'brand' => 'ASUS',
        'condition' => 'LIKE NEW',
        'estimated_market_value' => 55000,
        'forced_sale_value' => 35000,
        'marketplace_price' => 42000,
        'shop_location' => 'Shop 1',
        'status' => 'Listed',
        'entry_type' => 'CONSIGNMENT',
        'is_published_to_marketplace' => true,
        'notes' => 'Slim metallic design. Excellent battery backup, fast PCIe SSD.',
        'photo_urls_json' => '["assets/img/product-laptop-real.png"]'
    ],

    // TVs
    [
        'item_name' => 'Hisense 32" Smart Android LED TV',
        'category' => 'TVs',
        'brand' => 'Hisense',
        'condition' => 'GOOD',
        'estimated_market_value' => 19000,
        'forced_sale_value' => 12000,
        'marketplace_price' => 14500,
        'shop_location' => 'Shop 1',
        'status' => 'Listed',
        'entry_type' => 'DIRECT_PURCHASE',
        'is_published_to_marketplace' => true,
        'notes' => 'Preloaded with YouTube & Netflix. Remote & base stand included.',
        'photo_urls_json' => '["assets/img/product-tv-real.png"]'
    ],
    [
        'item_name' => 'LG 43" 4K Smart TV WebOS',
        'category' => 'TVs',
        'brand' => 'LG',
        'condition' => 'GOOD',
        'estimated_market_value' => 40000,
        'forced_sale_value' => 24000,
        'marketplace_price' => 28000,
        'shop_location' => 'Shop 2',
        'status' => 'Listed',
        'entry_type' => 'LOAN',
        'is_published_to_marketplace' => true,
        'notes' => 'Magic remote included. HDR10, stunning display, wall mount brackets.',
        'photo_urls_json' => '["assets/img/product-tv-real.png"]'
    ],
    [
        'item_name' => 'TCL 55" 4K UHD Smart Google TV',
        'category' => 'TVs',
        'brand' => 'TCL',
        'condition' => 'LIKE NEW',
        'estimated_market_value' => 60000,
        'forced_sale_value' => 30000,
        'marketplace_price' => 34000,
        'shop_location' => 'Shop 1',
        'status' => 'Listed',
        'entry_type' => 'CONSIGNMENT',
        'is_published_to_marketplace' => true,
        'notes' => 'Dolby Audio, Bluetooth, Google Play Store. Pristine condition.',
        'photo_urls_json' => '["assets/img/product-tv-real.png"]'
    ],

    // Audio
    [
        'item_name' => 'Sony 5.1 Home Theatre System (1000W)',
        'category' => 'Audio',
        'brand' => 'Sony',
        'condition' => 'GOOD',
        'estimated_market_value' => 22000,
        'forced_sale_value' => 11000,
        'marketplace_price' => 14000,
        'shop_location' => 'Shop 1',
        'status' => 'Listed',
        'entry_type' => 'DIRECT_PURCHASE',
        'is_published_to_marketplace' => true,
        'notes' => 'Deep thumping bass. 5 speakers + subwoofer. Bluetooth & USB working.',
        'photo_urls_json' => '["assets/img/product-audio-real.png"]'
    ],
    [
        'item_name' => 'JBL Go 3 Portable Waterproof Speaker',
        'category' => 'Audio',
        'brand' => 'JBL',
        'condition' => 'LIKE NEW',
        'estimated_market_value' => 7000,
        'forced_sale_value' => 3500,
        'marketplace_price' => 4800,
        'shop_location' => 'Shop 2',
        'status' => 'Listed',
        'entry_type' => 'LOAN',
        'is_published_to_marketplace' => true,
        'notes' => 'Original JBL sound. IP67 waterproof. Type-C fast charging.',
        'photo_urls_json' => '["assets/img/product-audio-real.png"]'
    ],

    // Fridges
    [
        'item_name' => 'Hisense 150L Single Door Direct Cool Fridge',
        'category' => 'Fridges',
        'brand' => 'Hisense',
        'condition' => 'GOOD',
        'estimated_market_value' => 20000,
        'forced_sale_value' => 13000,
        'marketplace_price' => 16000,
        'shop_location' => 'Shop 1',
        'status' => 'Listed',
        'entry_type' => 'DIRECT_PURCHASE',
        'is_published_to_marketplace' => true,
        'notes' => 'Freezer cools in minutes. Very clean interior, low power consumption.',
        'photo_urls_json' => '["assets/img/product-fridge-real.png"]'
    ],
    [
        'item_name' => 'Samsung 260L Double Door No-Frost Fridge',
        'category' => 'Fridges',
        'brand' => 'Samsung',
        'condition' => 'GOOD',
        'estimated_market_value' => 45000,
        'forced_sale_value' => 25000,
        'marketplace_price' => 30000,
        'shop_location' => 'Shop 2',
        'status' => 'Listed',
        'entry_type' => 'LOAN',
        'is_published_to_marketplace' => true,
        'notes' => 'Digital inverter technology. Big vegetable box, ice maker tray.',
        'photo_urls_json' => '["assets/img/product-fridge-real.png"]'
    ],

    // Cookers & Kitchen
    [
        'item_name' => 'Ramtons 2-Burner Gas Cooker Tabletop',
        'category' => 'Cookers',
        'brand' => 'Ramtons',
        'condition' => 'GOOD',
        'estimated_market_value' => 7500,
        'forced_sale_value' => 4000,
        'marketplace_price' => 5200,
        'shop_location' => 'Shop 1',
        'status' => 'Listed',
        'entry_type' => 'DIRECT_PURCHASE',
        'is_published_to_marketplace' => true,
        'notes' => 'Auto-ignition tested and firing smoothly. Gas pipe and regulator included.',
        'photo_urls_json' => '["assets/img/product-cooker-real.png"]'
    ],
    [
        'item_name' => 'Cosori 5.5L Digital Air Fryer (1700W)',
        'category' => 'Kitchen',
        'brand' => 'Cosori',
        'condition' => 'GOOD',
        'estimated_market_value' => 10000,
        'forced_sale_value' => 5000,
        'marketplace_price' => 7000,
        'shop_location' => 'Shop 2',
        'status' => 'Listed',
        'entry_type' => 'CONSIGNMENT',
        'is_published_to_marketplace' => true,
        'notes' => 'Non-stick basket spotless. 11 presets for chicken, chips, fish.',
        'photo_urls_json' => '["assets/img/product-kitchen-real.png"]'
    ],

    // Gaming
    [
        'item_name' => 'PlayStation 4 Slim 500GB + 2 Controllers',
        'category' => 'Gaming',
        'brand' => 'Sony',
        'condition' => 'GOOD',
        'estimated_market_value' => 28000,
        'forced_sale_value' => 16000,
        'marketplace_price' => 20000,
        'shop_location' => 'Shop 1',
        'status' => 'Listed',
        'entry_type' => 'DIRECT_PURCHASE',
        'is_published_to_marketplace' => true,
        'notes' => 'HDMI cable, power cord, 2 DualShock 4 pads. FIFA25 tested 100%.',
        'photo_urls_json' => '["assets/img/product-gaming-real.png"]'
    ],
    [
        'item_name' => 'Nintendo Switch OLED Console + JoyCons & Dock',
        'category' => 'Gaming',
        'brand' => 'Nintendo',
        'condition' => 'LIKE NEW',
        'estimated_market_value' => 38000,
        'forced_sale_value' => 22000,
        'marketplace_price' => 27500,
        'shop_location' => 'Shop 2',
        'status' => 'Listed',
        'entry_type' => 'LOAN',
        'is_published_to_marketplace' => true,
        'notes' => 'Vibrant 7-inch OLED screen. Glass protector applied since day 1.',
        'photo_urls_json' => '["assets/img/product-gaming-real.png"]'
    ]
];

$successCount = 0;
foreach ($products as $p) {
    // Check if item already exists by name
    $check = supabaseApiRequest('items?item_name=eq.' . urlencode($p['item_name']) . '&select=id');
    if ($check['success'] && !empty($check['data'])) {
        echo "Already exists: {$p['item_name']}\n";
        continue;
    }

    $res = supabaseApiRequest('items', 'POST', [$p], true);
    if ($res['success']) {
        echo "✓ Inserted: {$p['item_name']}\n";
        $successCount++;
    } else {
        echo "✗ Failed to insert {$p['item_name']}: {$res['error']}\n";
    }
}

echo "\nDone! Successfully inserted $successCount products into Supabase.\n";
