<?php
require_once __DIR__ . '/config/config.php';
require_once __DIR__ . '/includes/functions.php';

// ── Query params ────────────────────────────────────────────────
$category  = trim($_GET['category'] ?? 'All');
$shopLoc   = trim($_GET['shop_location'] ?? 'All');
$search    = trim($_GET['search'] ?? '');
$sort      = $_GET['sort'] ?? 'newest';
$page      = max(1, (int)($_GET['page'] ?? 1));
$limit     = ITEMS_PER_PAGE;

// ── Build query ─────────────────────────────────────────────────
$where  = ['is_published = 1', 'is_available = 1'];
$params = [];

if ($category !== 'All' && $category !== '') {
    $where[]       = 'category = :cat';
    $params[':cat'] = $category;
}
if ($shopLoc !== 'All' && $shopLoc !== '') {
    $where[]       = 'shop_location = :shop';
    $params[':shop'] = $shopLoc;
}
if ($search !== '') {
    $where[]          = '(item_name LIKE :s OR brand LIKE :s OR notes LIKE :s)';
    $params[':s']      = '%' . $search . '%';
}

$whereSQL = implode(' AND ', $where);
$orderSQL = match ($sort) {
    'price_asc'  => 'marketplace_price ASC',
    'price_desc' => 'marketplace_price DESC',
    'popular'    => 'views_count DESC',
    default      => 'updated_at DESC',
};

$db = null;
$items = [];
$total = 0;
$pages = 1;
$categoryCounts = [];

try {
    $db = getDB();

    // Total count
    $cs = $db->prepare("SELECT COUNT(*) FROM marketplace_items WHERE $whereSQL");
    $cs->execute($params);
    $total = (int)$cs->fetchColumn();
    $pages = (int)ceil($total / $limit);

    // Items
    $offset = ($page - 1) * $limit;
    $st = $db->prepare("
        SELECT id, remote_item_id, item_name, category, brand,
               condition_grade, marketplace_price, estimated_market_value,
               shop_location, shop_branch_name, is_available, photo_urls, views_count
        FROM marketplace_items
        WHERE $whereSQL
        ORDER BY $orderSQL
        LIMIT :lim OFFSET :off
    ");
    foreach ($params as $k => $v) $st->bindValue($k, $v);
    $st->bindValue(':lim', $limit, PDO::PARAM_INT);
    $st->bindValue(':off', $offset, PDO::PARAM_INT);
    $st->execute();
    $items = $st->fetchAll();
    foreach ($items as &$i) {
        $i['photo_urls'] = json_decode($i['photo_urls'] ?? '[]', true) ?: [];
    }

    // Category counts
    $cstmt = $db->query("SELECT category, COUNT(*) as cnt FROM marketplace_items WHERE is_published=1 AND is_available=1 GROUP BY category ORDER BY cnt DESC");
    $categoryCounts = $cstmt->fetchAll(PDO::FETCH_KEY_PAIR);

} catch (Exception $e) {
    // DB not set up yet — will show empty state
}

// ── SEO ─────────────────────────────────────────────────────────
$titlePart = $search ? "Search: {$search}" : ($category !== 'All' ? $category : 'All Products');
$pageTitle  = "{$titlePart} — MagTech Marketplace";
$pageDescription = "Browse {$titlePart} at MagTech Investments. Quality second-hand electronics in Kitengela — Chairman Road & Deliverance Road.";
$activePage = 'shop';

require_once __DIR__ . '/includes/header.php';
?>

<!-- ── FILTER BAR ──────────────────────────────────────────────── -->
<div class="filter-bar">
  <div class="filter-bar__inner container">

    <!-- Category chips -->
    <a href="<?= APP_URL ?>/shop" class="filter-chip <?= $category === 'All' ? 'is-active' : '' ?>">
      All
    </a>
    <?php foreach (CATEGORIES as $cat): ?>
    <a href="<?= APP_URL ?>/shop?category=<?= urlencode($cat) ?><?= $shopLoc !== 'All' ? '&shop_location='.urlencode($shopLoc) : '' ?>"
       class="filter-chip <?= $category === $cat ? 'is-active' : '' ?>">
      <?= categoryIcon($cat) ?> <?= htmlspecialchars($cat) ?>
      <?php if (isset($categoryCounts[$cat])): ?>
      <span style="opacity:.6;font-size:10px">(<?= $categoryCounts[$cat] ?>)</span>
      <?php endif; ?>
    </a>
    <?php endforeach; ?>

    <div class="filter-bar__divider"></div>

    <!-- Shop filter -->
    <a href="<?= APP_URL ?>/shop?<?= $category !== 'All' ? 'category='.urlencode($category).'&' : '' ?>shop_location=Shop+1"
       class="filter-chip <?= $shopLoc === 'Shop 1' ? 'is-active' : '' ?>">
      📍 Chairman
    </a>
    <a href="<?= APP_URL ?>/shop?<?= $category !== 'All' ? 'category='.urlencode($category).'&' : '' ?>shop_location=Shop+2"
       class="filter-chip <?= $shopLoc === 'Shop 2' ? 'is-active' : '' ?>">
      📍 Deliverance Rd
    </a>

    <!-- Sort -->
    <div class="filter-bar__sort">
      <select id="sort-select" class="filter-select" aria-label="Sort products">
        <option value="newest"     <?= $sort === 'newest'     ? 'selected' : '' ?>>Newest first</option>
        <option value="price_asc"  <?= $sort === 'price_asc'  ? 'selected' : '' ?>>Price: Low → High</option>
        <option value="price_desc" <?= $sort === 'price_desc' ? 'selected' : '' ?>>Price: High → Low</option>
        <option value="popular"    <?= $sort === 'popular'    ? 'selected' : '' ?>>Most viewed</option>
      </select>
    </div>
  </div>
</div>

<!-- ── SHOP PAGE ──────────────────────────────────────────────── -->
<main class="shop-page">
  <div class="container">

    <!-- Results header -->
    <div class="shop-results-header">
      <div>
        <?php if ($search): ?>
        <h1 class="section-heading" style="font-size:var(--text-2xl)">
          Results for "<?= htmlspecialchars($search) ?>"
        </h1>
        <?php elseif ($category !== 'All'): ?>
        <h1 class="section-heading" style="font-size:var(--text-2xl)">
          <?= categoryIcon($category) ?> <?= htmlspecialchars($category) ?>
        </h1>
        <?php else: ?>
        <h1 class="section-heading" style="font-size:var(--text-2xl)">All Products</h1>
        <?php endif; ?>

        <p class="shop-results-count" style="margin-top:.25rem">
          <strong><?= number_format($total) ?></strong> item<?= $total !== 1 ? 's' : '' ?> found
          <?php if ($shopLoc !== 'All'): ?>— in <?= htmlspecialchars($shopLoc) ?><?php endif; ?>
        </p>
      </div>

      <!-- Active filters pill row -->
      <div style="display:flex;gap:.5rem;flex-wrap:wrap;align-items:center">
        <?php if ($search): ?>
        <a href="<?= APP_URL ?>/shop" style="display:inline-flex;align-items:center;gap:.3rem;background:var(--teal-50);color:var(--teal-800);border:1.5px solid var(--teal-200);border-radius:var(--radius-full);padding:.25rem .75rem;font-size:12px;font-weight:600">
          "<?= htmlspecialchars($search) ?>" ✕
        </a>
        <?php endif; ?>
      </div>
    </div>

    <!-- Layout -->
    <div class="shop-layout">

      <!-- Sidebar -->
      <aside class="shop-sidebar" aria-label="Filter products">

        <div class="sidebar-section">
          <div class="sidebar-title">Category</div>
          <a href="<?= APP_URL ?>/shop" class="sidebar-option <?= $category === 'All' ? 'is-active' : '' ?>">
            All Categories
            <span class="sidebar-option__count"><?= number_format($db ? (int)$db->query("SELECT COUNT(*) FROM marketplace_items WHERE is_published=1 AND is_available=1")->fetchColumn() : 0) ?></span>
          </a>
          <?php foreach (CATEGORIES as $cat): ?>
          <a href="<?= APP_URL ?>/shop?category=<?= urlencode($cat) ?><?= $shopLoc !== 'All' ? '&shop_location='.urlencode($shopLoc) : '' ?>"
             class="sidebar-option <?= $category === $cat ? 'is-active' : '' ?>">
            <?= categoryIcon($cat) ?> <?= htmlspecialchars($cat) ?>
            <span class="sidebar-option__count"><?= $categoryCounts[$cat] ?? 0 ?></span>
          </a>
          <?php endforeach; ?>
        </div>

        <div class="sidebar-section">
          <div class="sidebar-title">Branch</div>
          <?php foreach (['All' => 'Both Branches', 'Shop 1' => '📍 Shop 1 (Chairman)', 'Shop 2' => '📍 Shop 2 (Deliverance Rd)'] as $val => $label): ?>
          <a href="<?= APP_URL ?>/shop?<?= $category !== 'All' ? 'category='.urlencode($category).'&' : '' ?>shop_location=<?= urlencode($val) ?>"
             class="sidebar-option <?= $shopLoc === $val ? 'is-active' : '' ?>">
            <?= htmlspecialchars($label) ?>
          </a>
          <?php endforeach; ?>
        </div>

        <div class="sidebar-section">
          <div class="sidebar-title">Condition</div>
          <?php foreach (['Like New', 'Good', 'Fair', 'For Parts'] as $cond): ?>
          <a href="<?= APP_URL ?>/shop?<?= $category !== 'All' ? 'category='.urlencode($category).'&' : '' ?>condition=<?= urlencode($cond) ?>"
             class="sidebar-option">
            <?= htmlspecialchars($cond) ?>
          </a>
          <?php endforeach; ?>
        </div>

      </aside>

      <!-- Products -->
      <div class="shop-content">
        <?php if (!empty($items)): ?>
        <div class="product-grid">
          <?php foreach ($items as $item):
            $photo = primaryPhoto($item);
            $shopNum = str_contains($item['shop_location'], '2') ? '2' : '1';
            $condClass = conditionClass($item['condition_grade']);
            $url = APP_URL . '/product?id=' . $item['remote_item_id'];
            $wl  = whatsappLink($item);
          ?>
          <article class="product-card <?= $condClass ?>"
                   data-url="<?= htmlspecialchars($url) ?>"
                   itemscope itemtype="https://schema.org/Product">

            <div class="product-card__image">
              <img src="<?= htmlspecialchars($photo) ?>"
                   alt="<?= htmlspecialchars($item['item_name']) ?>"
                   loading="lazy"
                   itemprop="image">

              <div class="product-card__badge-row">
                <span class="badge badge--shop<?= $shopNum ?>">
                  <?= htmlspecialchars($item['shop_location']) ?>
                </span>
                <button class="product-card__save"
                        data-save-btn
                        data-id="<?= $item['remote_item_id'] ?>"
                        data-name="<?= htmlspecialchars($item['item_name']) ?>"
                        aria-label="Save item">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                  </svg>
                </button>
              </div>
            </div>

            <div class="product-card__body">
              <div class="product-card__meta">
                <span class="badge badge--condition"><?= htmlspecialchars($item['condition_grade']) ?></span>
                <?php if ($item['brand']): ?>
                <span style="font-size:11px;color:var(--text-muted)"><?= htmlspecialchars($item['brand']) ?></span>
                <?php endif; ?>
              </div>

              <h2 class="product-card__name" itemprop="name">
                <?= htmlspecialchars($item['item_name']) ?>
              </h2>

              <div class="product-card__price-row">
                <span class="product-card__price" itemprop="price">
                  <?= formatPrice($item['marketplace_price']) ?>
                </span>
                <?php if ($item['estimated_market_value'] && $item['estimated_market_value'] > $item['marketplace_price']): ?>
                <span class="product-card__old-price"><?= formatPrice($item['estimated_market_value']) ?></span>
                <?php endif; ?>
              </div>

              <div class="product-card__actions">
                <a href="<?= htmlspecialchars($wl) ?>" target="_blank"
                   class="btn btn--primary btn--sm">WhatsApp</a>
                <a href="<?= htmlspecialchars($url) ?>"
                   class="btn btn--outline btn--sm">Details</a>
              </div>
            </div>
          </article>
          <?php endforeach; ?>
        </div>

        <!-- Pagination -->
        <?php if ($pages > 1): ?>
        <nav class="pagination" aria-label="Pagination">
          <?php if ($page > 1): ?>
          <a href="?<?= http_build_query(array_merge($_GET, ['page' => $page - 1])) ?>"
             class="page-btn" aria-label="Previous">←</a>
          <?php endif; ?>

          <?php for ($p = max(1, $page-2); $p <= min($pages, $page+2); $p++): ?>
          <a href="?<?= http_build_query(array_merge($_GET, ['page' => $p])) ?>"
             class="page-btn <?= $p === $page ? 'is-active' : '' ?>"
             aria-label="Page <?= $p ?>" <?= $p === $page ? 'aria-current="page"' : '' ?>>
            <?= $p ?>
          </a>
          <?php endfor; ?>

          <?php if ($page < $pages): ?>
          <a href="?<?= http_build_query(array_merge($_GET, ['page' => $page + 1])) ?>"
             class="page-btn" aria-label="Next">→</a>
          <?php endif; ?>
        </nav>
        <?php endif; ?>

        <?php else: ?>
        <div class="empty-state">
          <div class="empty-state__icon">🔍</div>
          <h2 class="empty-state__title">Hatuoni chochote hapa</h2>
          <p class="empty-state__body">
            <?php if ($search): ?>
              No results for "<?= htmlspecialchars($search) ?>". Try a different search or check the filters.
            <?php else: ?>
              No items in this category right now. Check back soon — stock changes daily.
            <?php endif; ?>
          </p>
          <div style="display:flex;gap:1rem;justify-content:center;flex-wrap:wrap">
            <a href="<?= APP_URL ?>/shop" class="btn btn--primary">View all products</a>
            <a href="https://wa.me/254712345678" class="btn btn--outline" target="_blank">Ask us on WhatsApp</a>
          </div>
        </div>
        <?php endif; ?>
      </div>
    </div>
  </div>
</main>

<?php require_once __DIR__ . '/includes/footer.php'; ?>
