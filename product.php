<?php
require_once __DIR__ . '/config/config.php';
require_once __DIR__ . '/includes/functions.php';

// Get item ID
$id = $_GET['id'] ?? null;

if (!$id) {
    header('Location: ' . APP_URL . '/shop');
    exit;
}

$item    = null;
$related = [];

try {
    $db = getDB();

    // Increment views
    $db->prepare("UPDATE marketplace_items SET views_count = views_count + 1 WHERE (id = ? OR remote_item_id = ?) AND is_published = 1")
       ->execute([$id, $id]);

    // Fetch item
    $stmt = $db->prepare("SELECT * FROM marketplace_items WHERE (id = ? OR remote_item_id = ?) AND is_published = 1 LIMIT 1");
    $stmt->execute([$id, $id]);
    $item = $stmt->fetch();

    if ($item) {
        $item['photo_urls'] = json_decode($item['photo_urls'] ?? '[]', true) ?: [];

        // Related
        $relstmt = $db->prepare("
            SELECT id, remote_item_id, item_name, brand, condition_grade,
                   marketplace_price, shop_location, photo_urls
            FROM marketplace_items
            WHERE category = ? AND is_published = 1 AND is_available = 1 AND id != ?
            ORDER BY updated_at DESC LIMIT 4
        ");
        $relstmt->execute([$item['category'], $item['id']]);
        $related = $relstmt->fetchAll();
        foreach ($related as &$r) {
            $r['photo_urls'] = json_decode($r['photo_urls'] ?? '[]', true) ?: [];
        }
    }
} catch (Exception $e) {
    // DB not set up
}

if (!$item) {
    // Item not found
    http_response_code(404);
    $pageTitle = '404 — Not Found | MagTech';
    require_once __DIR__ . '/includes/header.php';
    echo '<div class="container" style="padding:8rem 2rem;text-align:center">
            <div style="font-size:4rem;margin-bottom:2rem">😕</div>
            <h1 style="font-family:var(--font-display);font-size:2rem;margin-bottom:1rem">Item not found</h1>
            <p style="color:var(--text-secondary);margin-bottom:2rem">This item may have been sold or removed.</p>
            <a href="'.APP_URL.'/shop" class="btn btn--primary">Browse all products</a>
          </div>';
    require_once __DIR__ . '/includes/footer.php';
    exit;
}

// ── Build page meta ──────────────────────────────────────────────
$shopNum    = str_contains($item['shop_location'], '2') ? '2' : '1';
$shops      = SHOPS;
$shopKey    = $item['shop_location'];
$shopDetail = $shops[$shopKey] ?? null;
$primaryPhoto = primaryPhoto($item, APP_URL . '/assets/img/placeholder.jpg');
$condClass  = conditionClass($item['condition_grade']);
$wlLink     = whatsappLink($item);

$savings = ($item['estimated_market_value'] && $item['estimated_market_value'] > $item['marketplace_price'])
    ? $item['estimated_market_value'] - $item['marketplace_price']
    : 0;

$pageTitle       = htmlspecialchars($item['item_name']) . ' — KSh ' . number_format($item['marketplace_price']) . ' | MagTech';
$pageDescription = 'Buy ' . $item['item_name'] . ' in ' . $item['condition_grade'] . ' condition for KSh ' . number_format($item['marketplace_price']) . ' at MagTech ' . ($shopDetail['location'] ?? '') . '. Call or WhatsApp to confirm availability.';
$pageImage       = $primaryPhoto;
$activePage      = 'shop';

// Structured data
$headExtra = '<script type="application/ld+json">
' . json_encode([
    '@context' => 'https://schema.org',
    '@type'    => 'Product',
    'name'     => $item['item_name'],
    'brand'    => ['@type' => 'Brand', 'name' => $item['brand'] ?? ''],
    'image'    => $item['photo_urls'],
    'description' => $item['notes'] ?? '',
    'offers'   => [
        '@type'         => 'Offer',
        'price'         => $item['marketplace_price'],
        'priceCurrency' => 'KES',
        'availability'  => $item['is_available'] ? 'https://schema.org/InStock' : 'https://schema.org/OutOfStock',
        'seller'        => ['@type' => 'LocalBusiness', 'name' => 'MagTech Investments'],
    ],
], JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE) . '
</script>';

require_once __DIR__ . '/includes/header.php';
?>

<main class="product-page">
  <div class="container">

    <!-- Breadcrumb -->
    <nav class="breadcrumb" aria-label="Breadcrumb">
      <a href="<?= APP_URL ?>/">Home</a>
      <span class="breadcrumb__sep">›</span>
      <a href="<?= APP_URL ?>/shop">Shop</a>
      <span class="breadcrumb__sep">›</span>
      <a href="<?= APP_URL ?>/shop?category=<?= urlencode($item['category']) ?>">
        <?= htmlspecialchars($item['category']) ?>
      </a>
      <span class="breadcrumb__sep">›</span>
      <span class="breadcrumb__current"><?= htmlspecialchars($item['item_name']) ?></span>
    </nav>

    <!-- Product grid -->
    <div class="product-page__grid">

      <!-- ── GALLERY ─────────────────────────────────────────── -->
      <div class="product-gallery">
        <div class="gallery-main <?= $condClass ?>">
          <img id="gallery-main-img"
               src="<?= htmlspecialchars($primaryPhoto) ?>"
               alt="<?= htmlspecialchars($item['item_name']) ?>"
               loading="eager">

          <div class="gallery-main__badge">
            <span class="badge badge--shop<?= $shopNum ?>">
              📍 <?= htmlspecialchars($item['shop_branch_name'] ?? $item['shop_location']) ?>
            </span>
          </div>
        </div>

        <?php if (count($item['photo_urls']) > 1): ?>
        <div class="gallery-thumbs">
          <?php foreach ($item['photo_urls'] as $i => $url): ?>
          <div class="gallery-thumb <?= $i === 0 ? 'is-active' : '' ?>"
               data-src="<?= htmlspecialchars($url) ?>">
            <img src="<?= htmlspecialchars($url) ?>"
                 alt="<?= htmlspecialchars($item['item_name']) ?> view <?= $i+1 ?>"
                 loading="lazy">
          </div>
          <?php endforeach; ?>
        </div>
        <?php endif; ?>
      </div>

      <!-- ── PRODUCT INFO ───────────────────────────────────── -->
      <div class="product-info">

        <?php if ($item['brand']): ?>
        <div class="product-info__brand"><?= htmlspecialchars($item['brand']) ?></div>
        <?php endif; ?>

        <h1 class="product-info__title display-title">
          <?= htmlspecialchars($item['item_name']) ?>
        </h1>

        <!-- Badges -->
        <div class="product-info__badges">
          <span class="badge badge--condition <?= $condClass ?>">
            <?= htmlspecialchars($item['condition_grade']) ?>
          </span>
          <span class="badge badge--available">✓ Available</span>
          <span class="badge badge--shop<?= $shopNum ?>">
            📍 <?= htmlspecialchars($item['shop_location']) ?>
          </span>
        </div>

        <!-- Price block -->
        <div class="product-info__price-block">
          <div class="product-info__price">
            <?= formatPrice($item['marketplace_price']) ?>
          </div>
          <?php if ($savings > 0): ?>
          <div class="product-info__market-value">
            Market value: <span><?= formatPrice($item['estimated_market_value']) ?></span>
            <span class="savings-badge">Save <?= formatPrice($savings) ?></span>
          </div>
          <?php endif; ?>
        </div>

        <!-- Branch card -->
        <?php if ($shopDetail): ?>
        <div class="branch-card">
          <span class="branch-card__icon">📍</span>
          <div>
            <div class="branch-card__name"><?= htmlspecialchars($shopDetail['name']) ?></div>
            <div class="branch-card__addr"><?= htmlspecialchars($shopDetail['location']) ?> · <?= htmlspecialchars($shopDetail['hours']) ?></div>
          </div>
        </div>
        <?php endif; ?>

        <!-- Contact actions -->
        <div class="contact-actions">
          <a href="<?= htmlspecialchars($wlLink) ?>" target="_blank" rel="noopener"
             class="btn whatsapp-btn btn--lg btn--full">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true"><path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 0 1-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 0 1-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 0 1 2.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0 0 12.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 0 0 5.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 0 0-3.48-8.413z"/></svg>
            Ask about this on WhatsApp
          </a>

          <?php if ($shopDetail): ?>
          <a href="tel:<?= htmlspecialchars($shopDetail['phone']) ?>"
             class="btn call-btn btn--lg btn--full">
            📞 Call <?= htmlspecialchars($shopDetail['name']) ?>
          </a>
          <?php endif; ?>

          <button class="btn btn--ghost btn--lg btn--full"
                  data-save-btn
                  data-id="<?= $item['remote_item_id'] ?>"
                  data-name="<?= htmlspecialchars($item['item_name']) ?>"
                  id="detail-save-btn">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
            Save for later
          </button>
        </div>

        <!-- Description -->
        <?php if ($item['notes']): ?>
        <div class="product-description">
          <div class="product-description__title">About this item</div>
          <p class="product-description__text"><?= nl2br(htmlspecialchars($item['notes'])) ?></p>
        </div>
        <?php endif; ?>

        <!-- Specs table -->
        <div class="product-description">
          <div class="product-description__title">Details</div>
          <table class="specs-table" aria-label="Product specifications">
            <tbody>
              <tr><td>Category</td><td><?= htmlspecialchars($item['category']) ?></td></tr>
              <?php if ($item['brand']): ?>
              <tr><td>Brand</td><td><?= htmlspecialchars($item['brand']) ?></td></tr>
              <?php endif; ?>
              <tr><td>Condition</td><td><?= htmlspecialchars($item['condition_grade']) ?></td></tr>
              <tr><td>Location</td><td><?= htmlspecialchars($item['shop_branch_name'] ?? $item['shop_location']) ?></td></tr>
              <tr><td>Status</td>
                  <td><?= $item['is_available'] ? '<span style="color:#16a34a;font-weight:600">✓ In Stock</span>' : '<span style="color:#dc2626">Out of stock</span>' ?></td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Share -->
        <div style="display:flex;align-items:center;gap:.75rem;margin-top:1.5rem;padding-top:1.5rem;border-top:1px solid var(--grey-100)">
          <span style="font-size:var(--text-sm);color:var(--text-secondary)">Share:</span>
          <a href="https://wa.me/?text=Check+this+out+at+MagTech: <?= urlencode($item['item_name']) ?>+— <?= urlencode(APP_URL . '/product?id=' . $item['remote_item_id']) ?>"
             target="_blank" class="btn btn--ghost btn--sm">WhatsApp</a>
          <button onclick="navigator.clipboard.writeText(window.location.href).then(()=>window.magtechShowToast('Link copied!'))"
                  class="btn btn--ghost btn--sm">Copy link</button>
        </div>

      </div>
    </div>
  </div>

  <!-- Lightbox -->
  <div class="lightbox" id="lightbox" role="dialog" aria-label="Image lightbox">
    <img class="lightbox__img" id="lightbox-img" src="" alt="">
    <button class="lightbox__close" id="lightbox-close" aria-label="Close lightbox">✕</button>
  </div>
</main>

<!-- Related products -->
<?php if (!empty($related)): ?>
<section class="related-section">
  <div class="container">
    <div class="section-header section-header--row">
      <div>
        <span class="section-label">More in <?= htmlspecialchars($item['category']) ?></span>
        <h2 class="section-heading" style="font-size:var(--text-2xl)">You might also like</h2>
      </div>
      <a href="<?= APP_URL ?>/shop?category=<?= urlencode($item['category']) ?>" class="btn btn--outline btn--sm">See all →</a>
    </div>
    <div class="product-grid" style="grid-template-columns:repeat(4,1fr)">
      <?php foreach ($related as $r):
        $rPhoto = primaryPhoto($r);
        $rShopNum = str_contains($r['shop_location'], '2') ? '2' : '1';
        $rUrl = APP_URL . '/product?id=' . $r['remote_item_id'];
        $rwl  = whatsappLink($r);
      ?>
      <article class="product-card <?= conditionClass($r['condition_grade']) ?>"
               data-url="<?= htmlspecialchars($rUrl) ?>">
        <div class="product-card__image">
          <img src="<?= htmlspecialchars($rPhoto) ?>" alt="<?= htmlspecialchars($r['item_name']) ?>" loading="lazy">
          <div class="product-card__badge-row">
            <span class="badge badge--shop<?= $rShopNum ?>"><?= htmlspecialchars($r['shop_location']) ?></span>
            <button class="product-card__save" data-save-btn
                    data-id="<?= $r['remote_item_id'] ?>"
                    data-name="<?= htmlspecialchars($r['item_name']) ?>"
                    aria-label="Save">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
              </svg>
            </button>
          </div>
        </div>
        <div class="product-card__body">
          <div class="product-card__meta">
            <span class="badge badge--condition"><?= htmlspecialchars($r['condition_grade']) ?></span>
          </div>
          <h3 class="product-card__name"><?= htmlspecialchars($r['item_name']) ?></h3>
          <div class="product-card__price-row">
            <span class="product-card__price"><?= formatPrice($r['marketplace_price']) ?></span>
          </div>
          <div class="product-card__actions">
            <a href="<?= htmlspecialchars($rwl) ?>" target="_blank" class="btn btn--primary btn--sm">WhatsApp</a>
            <a href="<?= htmlspecialchars($rUrl) ?>" class="btn btn--outline btn--sm">View</a>
          </div>
        </div>
      </article>
      <?php endforeach; ?>
    </div>
  </div>
</section>
<?php endif; ?>

<?php require_once __DIR__ . '/includes/footer.php'; ?>
